package com.example.playlistmaker.playlists.domain.state

import com.example.playlistmaker.playlists.domain.model.Playlist

sealed interface PlaylistsState {
    object Loading : PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

    data class Empty(
        val message: String
    ) : PlaylistsState
}