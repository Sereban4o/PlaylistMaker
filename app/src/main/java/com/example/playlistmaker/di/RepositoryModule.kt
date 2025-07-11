package com.example.playlistmaker.di

import com.example.playlistmaker.database.data.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.database.data.db.convertor.PlaylistTrackDbConvertor
import com.example.playlistmaker.database.data.db.convertor.TrackConvertor
import com.example.playlistmaker.database.data.db.convertor.TrackDbConvertor
import com.example.playlistmaker.favorites.domain.repository.FavoritesRepository
import com.example.playlistmaker.favorites.domain.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.playlists.domain.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    factory { PlaylistTrackDbConvertor() }

    factory { TrackConvertor() }
}