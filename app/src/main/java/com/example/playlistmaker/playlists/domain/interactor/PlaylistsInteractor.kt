package com.example.playlistmaker.playlists.domain.interactor

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun checkTrackInPlaylist(trackId: String, playlistId: Int): Flow<List<PlaylistTrack>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
    fun getTracks(playlistId: Int): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int)
    suspend fun deletePlaylist(playlistId: Int)
    fun getPlaylistById(playlistId: Int): Flow<List<Playlist>>
}

