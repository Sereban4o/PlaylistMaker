package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.LAST_TRACKS
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.SearchResponse
import com.example.playlistmaker.search.data.repository.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SearchRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as SearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate?.let { e ->
                            SimpleDateFormat("yyyy", Locale.getDefault()).format(e)
                        },
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun getHistory(): List<Track> {
        val historyTracks = getArrayFromJSON()

        return historyTracks

    }

    override fun addToHistory(
        track: Track
    ) {
        val historyTracks = getArrayFromJSON()

        val findTrack = historyTracks.find { it.trackId == track.trackId }

        if (findTrack != null) {
            historyTracks.remove(findTrack)
        }

        if (historyTracks.size == 10) {
            historyTracks.removeAt(9)
        }

        historyTracks.add(0, track)
        val newString = gson.toJson((historyTracks))
        sharedPreferences.edit() { putString(LAST_TRACKS, newString) }
    }

    override fun clearHistory() {
        val historyTracks = getArrayFromJSON()

        historyTracks.clear()
        val newString = gson.toJson((historyTracks))
        sharedPreferences.edit() { putString(LAST_TRACKS, newString) }
    }

    private fun getArrayFromJSON(): MutableList<Track> {
        val stringHistoryTracks = sharedPreferences.getString(LAST_TRACKS, "")!!
        val typeToken = object : TypeToken<MutableList<Track>>() {}.type
        var historyTracks: MutableList<Track> = mutableListOf()

        if (stringHistoryTracks.isNotBlank()) {
            historyTracks = gson.fromJson(stringHistoryTracks, typeToken)
        }
        return historyTracks
    }
}