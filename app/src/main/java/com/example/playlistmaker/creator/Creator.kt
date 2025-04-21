package com.example.playlistmaker.creator

import com.example.playlistmaker.data.ExternalNavigationRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.ExternalNavigationInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun externalNavigation(): ExternalNavigationInteractor {
        return ExternalNavigationRepositoryImpl()
    }

    fun provideExternalNavigation(): ExternalNavigationInteractor {
        return externalNavigation()
    }

}