package com.example.playlistmaker.settings.domain.interactor

interface SettingsInteractor {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
    fun switchTheme()
}