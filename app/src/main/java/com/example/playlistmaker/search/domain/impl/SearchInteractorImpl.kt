package com.example.playlistmaker.search.domain.impl

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.data.repository.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: SearchInteractor.TracksConsumer
    ) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }

            }

        }
    }

    override fun addToHistory(track: Track, sharedPrefs: SharedPreferences) {
        executor.execute { repository.addToHistory(track, sharedPrefs) }
    }

    override fun getHistory(
        sharedPrefs: SharedPreferences,
        consumer: SearchInteractor.TracksHistory
    ) {
        executor.execute {
            consumer.consume(repository.getHistory(sharedPrefs))
        }
    }

    override fun clearHistory(sharedPrefs: SharedPreferences) {
        executor.execute { repository.clearHistory(sharedPrefs) }
    }
}