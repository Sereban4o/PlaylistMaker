package com.example.playlistmaker.favorites.domain.impl

import com.example.playlistmaker.favorites.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {
    override fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }

    override suspend fun addFavorite(track: Track) {
        return favoritesRepository.addFavorite(track)
    }

    override suspend fun checkFavorite(trackId: String): Boolean {
        return favoritesRepository.checkFavorite(trackId)
    }

    override suspend fun deleteFavorite(trackId: String) {
        return favoritesRepository.deleteFavorite(trackId)
    }
}