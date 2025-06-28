package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


const val PLAYLIST_PREF = "PLAYLIST_PREF"
const val NIGHT_MODE = ""
const val LAST_TRACKS = "LAST_TRACK"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }

        val settingsInteractor: SettingsInteractor = getKoin().get()
        settingsInteractor.switchTheme()

        PermissionRequester.initialize(applicationContext)
    }
}
