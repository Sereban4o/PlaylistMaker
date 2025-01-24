package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory {

    private val gson = Gson()

    fun add(track: Track, sharedPrefs: SharedPreferences) {

        val lastTracks = getArrayFromJSON(sharedPrefs)

        val findTrack = lastTracks.find { it.trackId == track.trackId }

        if (findTrack != null) {
            lastTracks.remove(findTrack)
        }

        if (lastTracks.size == 10) {
            lastTracks.removeAt(9)
        }

        lastTracks.add(0, track)
        val newString = gson.toJson((lastTracks))
        sharedPrefs.edit().putString(LAST_TRACKS, newString).apply()
    }

    fun get(sharedPrefs: SharedPreferences): MutableList<Track> {

        val lastTracks = getArrayFromJSON(sharedPrefs)

        return lastTracks

    }

    fun clear(sharedPrefs: SharedPreferences) {

        val lastTracks = getArrayFromJSON(sharedPrefs)

        lastTracks.clear()
        val newString = gson.toJson((lastTracks))
        sharedPrefs.edit().putString(LAST_TRACKS, newString).apply()
    }

    private fun getArrayFromJSON(sharedPrefs: SharedPreferences): MutableList<Track> {
        val stringLastTracks = sharedPrefs.getString(LAST_TRACKS, "")!!
        val typeToken = object : TypeToken<MutableList<Track>>() {}.type
        var lastTracks: MutableList<Track> = mutableListOf()

        if (stringLastTracks.isNotBlank()) {
            lastTracks = gson.fromJson(stringLastTracks, typeToken)
        }
        return lastTracks
    }

}