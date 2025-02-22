package com.example.playlistmaker

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
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
): Parcelable
