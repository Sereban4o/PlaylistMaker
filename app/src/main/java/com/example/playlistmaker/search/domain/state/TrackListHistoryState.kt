package com.example.playlistmaker.search.domain.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface TrackListHistoryState {

    data class Content(val tracks: List<Track>?) : TrackListHistoryState
    companion object
}