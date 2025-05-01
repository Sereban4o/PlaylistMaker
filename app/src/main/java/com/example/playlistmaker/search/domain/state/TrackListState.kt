package com.example.playlistmaker.search.domain.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface TrackListState {
    object Loading : TrackListState
    data class Content(val tracks: List<Track>?) : TrackListState
    data class Error(val errorMessage: String?) : TrackListState
    data class Empty(val emptyMessage: String?) : TrackListState
    companion object
}