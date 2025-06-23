package com.example.playlistmaker.favorites.domain.impl

import com.example.playlistmaker.database.data.db.AppDatabase
import com.example.playlistmaker.database.data.db.TrackDbConvertor
import com.example.playlistmaker.database.data.db.entity.TrackFavoriteEntity
import com.example.playlistmaker.favorites.domain.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {
    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavorites()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackFavoriteEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    override suspend fun addFavorite(track: Track) {
        appDatabase.trackDao().addFavorite(trackDbConvertor.map(track))
    }

    override suspend fun checkFavorite(trackId: String): Boolean {
        val result = appDatabase.trackDao().checkFavorite(trackId)
        return result.isNotEmpty()
    }

    override suspend fun deleteFavorite(trackId: String) {
        appDatabase.trackDao().deleteFavorite(trackId)
    }
}