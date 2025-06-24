package com.example.playlistmaker.favorites.domain.repository

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavorites(): Flow<List<Track>>
    suspend fun addFavorite(track: Track)
    suspend fun deleteFavorite(trackId: String)
    suspend fun checkFavorite(trackId: String): Boolean
}