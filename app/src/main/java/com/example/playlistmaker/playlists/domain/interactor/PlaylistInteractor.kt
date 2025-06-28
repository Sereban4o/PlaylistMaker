package com.example.playlistmaker.playlists.domain.interactor

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    fun checkTrackInPlaylist(trackId: String, playlistId: Int): Flow<List<PlaylistTrack>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
}