package com.example.playlistmaker.settings.data.repository

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
    fun switchTheme()
}