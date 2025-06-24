package com.example.playlistmaker.favorites.domain.interactor

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun getFavorites(): Flow<List<Track>>
    suspend fun addFavorite(track: Track)
    suspend fun checkFavorite(trackId: String): Boolean
    suspend fun deleteFavorite(trackId: String)
}