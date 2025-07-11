package com.example.playlistmaker.playlists.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            addPlaylistSuspend(playlist)
        }
    }

    private suspend fun addPlaylistSuspend(playlist: Playlist) {
        playlistsInteractor.addPlaylist(playlist)
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist)
        }
    }
}