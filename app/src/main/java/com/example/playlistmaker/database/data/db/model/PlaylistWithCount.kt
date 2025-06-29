package com.example.playlistmaker.database.data.db.model

data class PlaylistWithCount(
    val id: Int,
    val name: String,
    val note: String,
    val imageUri: String,
    val countTracks: Int
) {}