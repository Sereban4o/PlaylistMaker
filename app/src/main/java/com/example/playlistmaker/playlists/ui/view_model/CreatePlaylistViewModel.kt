package com.example.playlistmaker.playlists.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            addPlaylistSuspend(playlist)
        }

    }

    private suspend fun addPlaylistSuspend(playlist: Playlist) {
        playlistInteractor.addPlaylist(playlist)
    }
}