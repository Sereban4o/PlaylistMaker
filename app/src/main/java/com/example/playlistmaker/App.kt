package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_PREF = "PLAYLIST_PREF"
const val NIGHT_MODE = ""
const val LAST_TRACKS = "LAST_TRACK"

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
        val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = darkModeFlags == Configuration.UI_MODE_NIGHT_YES

        darkTheme = sharedPrefs.getBoolean(NIGHT_MODE, isDarkModeOn)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(NIGHT_MODE, darkTheme).apply()

    }
}
