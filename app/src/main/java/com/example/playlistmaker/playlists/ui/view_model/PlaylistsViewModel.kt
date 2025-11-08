package com.example.playlistmaker.playlists.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.state.PlaylistsState
import kotlinx.coroutines.launch


class PlaylistsViewModel(
    application: Application,
    private val playlistsInteractor: PlaylistsInteractor
) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<PlaylistsState>()

    init {
        fillData()
    }

    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun fillData() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty(getApplication<Application>().getString(R.string.empty_playlists)))
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }
}