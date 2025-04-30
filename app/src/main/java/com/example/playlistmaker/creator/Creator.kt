package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.data.repository.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.ExternalNavigatorInteractor


object Creator {
    private fun getTracksRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTracksRepository(context))
    }

    private fun getExternalNavigator(application: Application): ExternalNavigatorInteractor {
        return ExternalNavigatorImpl(application)
    }

    fun provideExternalNavigator(application: Application): ExternalNavigatorInteractor {
        return getExternalNavigator(application)
    }

    private fun getSettings(application: Application): SettingsInteractor {
        return SettingsInteractorImpl(application)
    }

    fun provideSettings(application: Application): SettingsInteractor {
        return getSettings(application)

    }


}