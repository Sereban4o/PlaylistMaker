package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.PLAYLIST_PREF
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.example.playlistmaker.search.domain.state.TrackListState

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()


        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val tracksInteractor = Creator.provideTracksInteractor(getApplication<Application>())
    private var stateLiveData = MutableLiveData<TrackListState>()
    private var stateHistoryLiveData = MutableLiveData<TrackListHistoryState>()
    private var searchText: String? = ""

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (changedText == searchText) {
            return
        }

        this.searchText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchTracks(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun observeState(): LiveData<TrackListState> = stateLiveData
    fun observeHistoryState(): LiveData<TrackListHistoryState> = stateHistoryLiveData

    private fun searchTracks(expression: String) {
        if (expression.isEmpty()) {
            return
        }
        stateLiveData.postValue(TrackListState.Loading)

        tracksInteractor.searchTracks(
            expression, object :
                SearchInteractor.TracksConsumer {

                override fun consume(
                    foundTracks: List<Track>?,
                    errorMessage: String?
                ) {

                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                TrackListState.Error(
                                    errorMessage = getApplication<Application>().getString(R.string.errorSearch)
                                )
                            )

                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TrackListState.Empty(
                                    emptyMessage = getApplication<Application>().getString(R.string.emptySearch)
                                )
                            )
                        }

                        else -> {
                            renderState(
                                TrackListState.Content(
                                    tracks = tracks
                                )
                            )
                        }
                    }

                }
            }


        )
    }

    fun searchHistory() {
        tracksInteractor.getHistory(
            object :
                SearchInteractor.TracksHistory {
                override fun consume(history: List<Track>) {
                    val tracks = mutableListOf<Track>()
                    tracks.addAll(history)
                    renderHistoryState(
                        TrackListHistoryState.Content(
                            tracks = tracks
                        )
                    )
                }
            })
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
    }

    fun addToHistory(track: Track) {
        tracksInteractor.addToHistory(
            track
        )
    }

    private fun renderState(state: TrackListState) {
        stateLiveData.postValue(state)
    }

    private fun renderHistoryState(state: TrackListHistoryState) {
        stateHistoryLiveData.postValue(state)
    }

}

