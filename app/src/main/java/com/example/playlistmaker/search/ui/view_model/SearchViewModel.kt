package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.example.playlistmaker.search.domain.state.TrackListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var stateLiveData = MutableLiveData<TrackListState>()
    private var stateHistoryLiveData = MutableLiveData<TrackListHistoryState>()
    private var searchText: String? = ""
    private var searchJob: Job? = null


    fun searchDebounce(changedText: String) {

        if (searchText == changedText) {
            return
        }

        searchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(changedText)
        }
    }

    fun clearSearch() {
        stateLiveData.postValue(TrackListState.Content(tracks = mutableListOf<Track>()))
    }

    fun observeState(): LiveData<TrackListState> = stateLiveData
    fun observeHistoryState(): LiveData<TrackListHistoryState> = stateHistoryLiveData

    private fun searchTracks(expression: String) {
        if (expression.isNotEmpty()) {
            stateLiveData.postValue(TrackListState.Loading)
            viewModelScope.launch {
                searchInteractor.searchTracks(expression)
                    .collect { pair ->
                        val tracks = mutableListOf<Track>()
                        val foundTracks = pair.first
                        val errorMessage = pair.second
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
        }
    }

    fun searchHistory() {
        searchInteractor.getHistory(
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
        searchInteractor.clearHistory()
    }

    fun addToHistory(track: Track) {
        searchInteractor.addToHistory(
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

