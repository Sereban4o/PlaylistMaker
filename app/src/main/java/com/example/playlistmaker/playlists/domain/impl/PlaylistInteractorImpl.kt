package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.playlists.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        return playlistRepository.addPlaylist(playlist)
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

}