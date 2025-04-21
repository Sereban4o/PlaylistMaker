package com.example.playlistmaker.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>

    fun getHistory(sharedPrefs: SharedPreferences): List<Track>

    fun addToHistory(track: Track, sharedPrefs: SharedPreferences)

    fun clearHistory(sharedPrefs: SharedPreferences)
}