package com.example.playlistmaker.favorites.domain.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {

    object Loading : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    data class Empty(
        val message: String
    ) : FavoritesState
}