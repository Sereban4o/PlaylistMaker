package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: String? = "",
    val trackName: String? = "",
    val artistName: String? = "",
    val trackTimeMillis: String? = "",
    val artworkUrl100: String? = "",
    val collectionName: String? = "",
    val releaseDate: String? = "",
    val primaryGenreName: String? = "",
    val country: String? = "",
    val previewUrl: String? = ""
) : Parcelable
