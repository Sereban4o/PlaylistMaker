package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.data.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): Boolean {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
         repository.updateThemeSetting(darkTheme)
    }

    override fun switchTheme() {
         repository.switchTheme()
    }
}