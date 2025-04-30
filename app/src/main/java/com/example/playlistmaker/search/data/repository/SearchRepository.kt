package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

    fun getHistory(sharedPrefs: SharedPreferences): List<Track>

    fun addToHistory(track: Track, sharedPrefs: SharedPreferences)

    fun clearHistory(sharedPrefs: SharedPreferences)
}