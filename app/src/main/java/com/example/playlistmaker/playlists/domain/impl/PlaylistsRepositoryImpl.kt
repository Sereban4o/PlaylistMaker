package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.database.data.db.AppDatabase
import com.example.playlistmaker.database.data.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.database.data.db.convertor.PlaylistTrackDbConvertor
import com.example.playlistmaker.database.data.db.convertor.TrackConvertor
import com.example.playlistmaker.database.data.db.entity.PlaylistEntity
import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.database.data.db.model.PlaylistWithCount
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor,
    private val trackConvertor: TrackConvertor
) : PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { list: List<PlaylistWithCount> ->
            list.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
        }
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().addPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlist))
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

    override fun getTracks(playlistId: Int): Flow<List<Track>> {
        return appDatabase.playlistTrackDao().getTracks(playlistId)
            .map { list: List<PlaylistTrackEntity> ->
                list.map { playlistTrack -> trackConvertor.map(playlistTrack) }
            }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        appDatabase.playlistTrackDao().deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
        appDatabase.playlistTrackDao().deleteAllTrackFromPlaylist(playlistId)
    }

    override fun getPlaylistById(playlistId: Int): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylistById(playlistId)
            .map { list: List<PlaylistEntity> ->
                list.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
            }
    }
}

