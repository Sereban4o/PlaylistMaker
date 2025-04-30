package com.example.playlistmaker.search.domain.interactor

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun getHistory(sharedPrefs: SharedPreferences, consumer: TracksHistory)

    fun addToHistory(track: Track, sharedPrefs: SharedPreferences)

    fun clearHistory(sharedPrefs: SharedPreferences)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    interface TracksHistory {
        fun consume(history: List<Track>)
    }
}