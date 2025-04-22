package com.example.playlistmaker.domain.impl


import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer,
        error: TracksInteractor.ErrorConsumer
    ) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (e: Exception) {
                error.consume(true)
            }
        }
    }

    override fun addToHistory(track: Track, sharedPrefs: SharedPreferences) {
        executor.execute { repository.addToHistory(track, sharedPrefs) }
    }

    override fun getHistory(
        sharedPrefs: SharedPreferences,
        consumer: TracksInteractor.TracksHistory
    ) {
        executor.execute {
            consumer.consume(repository.getHistory(sharedPrefs))
        }
    }

    override fun clearHistory(sharedPrefs: SharedPreferences) {
        executor.execute { repository.clearHistory(sharedPrefs) }
    }
}