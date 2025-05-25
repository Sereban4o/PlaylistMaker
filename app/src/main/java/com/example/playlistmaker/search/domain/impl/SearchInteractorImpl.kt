package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.data.repository.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String
    ): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addToHistory(track: Track) {
        executor.execute { repository.addToHistory(track) }
    }

    override fun getHistory(
        consumer: SearchInteractor.TracksHistory
    ) {
        executor.execute {
            consumer.consume(repository.getHistory())
        }
    }

    override fun clearHistory() {
        executor.execute { repository.clearHistory() }
    }
}