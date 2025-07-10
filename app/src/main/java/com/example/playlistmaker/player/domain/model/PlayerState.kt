package com.example.playlistmaker.player.domain.model

import com.example.playlistmaker.playlists.domain.model.Playlist

data class PlayerState(
    val progress: Float,
    var isPlaying: Boolean,
    val state: Int,
    val isFavorite: Boolean,
    val playlists: List<Playlist>
) {

}