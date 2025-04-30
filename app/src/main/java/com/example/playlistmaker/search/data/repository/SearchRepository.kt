package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

    fun getHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}