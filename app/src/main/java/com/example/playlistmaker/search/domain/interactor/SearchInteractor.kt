package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun getHistory(consumer: TracksHistory)

    fun addToHistory(track: Track)

    fun clearHistory()

    interface TracksHistory {
        fun consume(history: List<Track>)
    }
}