package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistRepository: PlaylistsRepository
) : PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        return playlistRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistRepository.updatePlaylist(playlist)
    }

    override fun checkTrackInPlaylist(
        trackId: String,
        playlistId: Int
    ): Flow<List<PlaylistTrack>> {
        return playlistRepository.checkTrackInPlaylist(trackId, playlistId)
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlistId: Int
    ) {
        return playlistRepository.addTrackToPlaylist(track, playlistId)
    }

    override fun getTracks(playlistId: Int): Flow<List<Track>> {
        return playlistRepository.getTracks(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        return playlistRepository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        return playlistRepository.deletePlaylist(playlistId)
    }

    override fun getPlaylistById(playlistId: Int): Flow<List<Playlist>> {
        return playlistRepository.getPlaylistById(playlistId)
    }

}