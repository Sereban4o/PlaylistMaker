package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.PLAYLIST_PREF
import com.example.playlistmaker.database.data.db.AppDatabase
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.SearchApiService
import org.koin.android.ext.koin.androidApplication
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SearchApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApiService::class.java)
    }

    single {
        androidApplication()
            .getSharedPreferences(PLAYLIST_PREF, Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single<NetworkClient> { RetrofitNetworkClient(androidApplication(), get()) }


}