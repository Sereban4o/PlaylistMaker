package com.example.playlistmaker.search.data.dto

import java.util.Date

data class TrackDto(
    val trackId: String?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)