package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.utils.SingleEventLiveData

class SettingsViewModel(
    private val externalNavigatorInteractor: ExternalNavigatorInteractor,
    private val settingsInteractor: SettingsInteractor,
    application: Application
) : AndroidViewModel(application) {

//    private val currentThemeSetting = SingleEventLiveData<Boolean>()
//
//    init {
//        currentThemeSetting.value = settingsInteractor.getThemeSettings()
//    }

    fun getCurrentTheme(): Boolean = settingsInteractor.getThemeSettings()

    fun updateThemeSetting(darkTheme: Boolean) {
      //  Log.d("statetest", darkTheme.toString())
        settingsInteractor.updateThemeSetting(darkTheme)
    }

    fun shareLink(url: String) {
        externalNavigatorInteractor.shareLink(url)
    }

    fun openLink(url: String) {
        externalNavigatorInteractor.openLink(url)
    }

    fun openEmail(emailData: EmailData) {
        externalNavigatorInteractor.openEmail(emailData)
    }
}