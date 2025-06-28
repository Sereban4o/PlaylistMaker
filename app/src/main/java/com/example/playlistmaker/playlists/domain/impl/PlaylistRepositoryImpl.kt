package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.database.data.db.AppDatabase
import com.example.playlistmaker.database.data.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.database.data.db.convertor.PlaylistTrackDbConvertor
import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.database.data.db.model.PlaylistWithCount
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.playlists.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { list: List<PlaylistWithCount> ->
            list.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
        }
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().addPlaylist(playlistDbConvertor.map(playlist))
    }

    override fun checkTrackInPlaylist(
        trackId: String,
        playlistId: Int
    ): Flow<List<PlaylistTrack>> {
        return appDatabase.playlistTrackDao().checkTrackInPlaylist(trackId, playlistId)
            .map { list: List<PlaylistTrackEntity> ->
                list.map { playlistTrack -> playlistTrackDbConvertor.map(playlistTrack) }
            }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlistId: Int
    ) {
        appDatabase.playlistTrackDao().addTrack(playlistTrackDbConvertor.map(track, playlistId))
    }

}

