package com.example.playlistmaker.playlists.domain.model

data class PlaylistTrack(
    val trackId: String = "",
    val playlistId: Int = 0,
    val trackName: String = "",
    val artistName: String = "",
    val trackTimeMillis: String = "",
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String = "",
    val primaryGenreName: String = "",
    val country: String = "",
    val previewUrl: String = ""
)