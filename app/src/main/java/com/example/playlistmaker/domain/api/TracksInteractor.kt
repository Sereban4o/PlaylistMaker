package com.example.playlistmaker.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer, error: ErrorConsumer)

    fun getHistory(sharedPrefs: SharedPreferences, consumer: TracksHistory)

    fun addToHistory(track: Track, sharedPrefs: SharedPreferences)

    fun clearHistory(sharedPrefs: SharedPreferences)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }

    interface ErrorConsumer {
        fun consume(error: Boolean)
    }

    interface TracksHistory {
        fun consume(history: List<Track>)
    }
}