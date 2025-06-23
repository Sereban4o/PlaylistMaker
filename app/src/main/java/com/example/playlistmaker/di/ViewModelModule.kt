package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.view_model.LibraryViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.ui.view_model.TrackViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel { (track: Track) ->
        TrackViewModel(get(), get())
    }

    viewModel {
        LibraryViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }
    viewModel {
        FavoritesViewModel(androidContext(), get())
    }
}