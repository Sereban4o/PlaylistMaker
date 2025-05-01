package com.example.playlistmaker.settings.data.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.NIGHT_MODE
import com.example.playlistmaker.PLAYLIST_PREF
import com.example.playlistmaker.settings.data.repository.SettingsRepository

class SettingsRepositoryImpl(application: Application): SettingsRepository {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
    private val isDarkModeOn =
        (application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    override fun getThemeSettings(): Boolean {
        val darkTheme = sharedPreferences.getBoolean(NIGHT_MODE, isDarkModeOn)
        return darkTheme
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
        sharedPreferences.edit() {
            putBoolean(NIGHT_MODE, darkTheme)
        }
        switchTheme()
    }

    override fun switchTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getThemeSettings()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}