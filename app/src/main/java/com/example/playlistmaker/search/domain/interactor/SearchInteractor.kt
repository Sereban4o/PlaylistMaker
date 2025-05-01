package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun getHistory(consumer: TracksHistory)

    fun addToHistory(track: Track)

    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    interface TracksHistory {
        fun consume(history: List<Track>)
    }
}