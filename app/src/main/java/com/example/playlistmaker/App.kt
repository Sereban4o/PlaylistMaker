package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator

const val PLAYLIST_PREF = "PLAYLIST_PREF"
const val NIGHT_MODE = ""
const val LAST_TRACKS = "LAST_TRACK"
const val TRACK_VIEW = "TRACK_VIEW"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettings(this)
        settingsInteractor.switchTheme()
    }

}
