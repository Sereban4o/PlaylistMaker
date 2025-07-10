package com.example.playlistmaker.playlists.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int = 0,
    val name: String = "",
    val note: String = "",
    val imageUri: String = "",
    val countTracks: Int = 0
) : Parcelable {
}