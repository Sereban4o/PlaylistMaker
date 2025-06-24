package com.example.playlistmaker.favorites.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.state.FavoritesState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val context: Context,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()

    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun fillData() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty(context.getString(R.string.empty_favorite)))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }

}