package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.LAST_TRACKS
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale


class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private val gson = Gson()
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.isSuccessful) {
            return (response as TracksSearchResponse).results.map {
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
            }
        } else {
            return emptyList()
        }
    }

    override fun getHistory(sharedPrefs: SharedPreferences): List<Track> {
        val historyTracks = getArrayFromJSON(sharedPrefs)

        return historyTracks

    }

    override fun addToHistory(
        track: Track,
        sharedPrefs: SharedPreferences
    ) {
        val historyTracks = getArrayFromJSON(sharedPrefs)

        val findTrack = historyTracks.find { it.trackId == track.trackId }

        if (findTrack != null) {
            historyTracks.remove(findTrack)
        }

        if (historyTracks.size == 10) {
            historyTracks.removeAt(9)
        }

        historyTracks.add(0, track)
        val newString = gson.toJson((historyTracks))
        sharedPrefs.edit() { putString(LAST_TRACKS, newString) }
    }

    override fun clearHistory(sharedPrefs: SharedPreferences) {
        val historyTracks = getArrayFromJSON(sharedPrefs)

        historyTracks.clear()
        val newString = gson.toJson((historyTracks))
        sharedPrefs.edit() { putString(LAST_TRACKS, newString) }
    }

    private fun getArrayFromJSON(sharedPrefs: SharedPreferences): MutableList<Track> {
        val stringHistoryTracks = sharedPrefs.getString(LAST_TRACKS, "")!!
        val typeToken = object : TypeToken<MutableList<Track>>() {}.type
        var historyTracks: MutableList<Track> = mutableListOf()

        if (stringHistoryTracks.isNotBlank()) {
            historyTracks = gson.fromJson(stringHistoryTracks, typeToken)
        }
        return historyTracks
    }
}