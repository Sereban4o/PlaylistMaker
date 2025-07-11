package com.example.playlistmaker.playlists.domain.state

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

data class PlaylistState(
    val playlist: Playlist,
    val trackList: List<Track>,
    val count: Int,
    val allTime: String
)