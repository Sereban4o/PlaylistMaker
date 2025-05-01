package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.utils.SingleEventLiveData

class SettingsViewModel(
    private val sharingInteractor: ExternalNavigatorInteractor,
    private val settingsInteractor: SettingsInteractor,
    application: Application
) : AndroidViewModel(application) {

    private val currentThemeSetting = SingleEventLiveData<Boolean>()

    init {
        currentThemeSetting.value = settingsInteractor.getThemeSettings()
    }

    fun getCurrentTheme(): LiveData<Boolean> = currentThemeSetting

    fun updateThemeSetting(darkTheme: Boolean) {
        settingsInteractor.updateThemeSetting(darkTheme)
    }

    fun shareLink(url: String) {
        sharingInteractor.shareLink(url)
    }

    fun openLink(url: String) {
        sharingInteractor.openLink(url)
    }

    fun openEmail(emailData: EmailData) {
        sharingInteractor.openEmail(emailData)
    }

    companion object {
        fun getViewModelFactory() =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as Application
                    val interactor = Creator.provideExternalNavigator(app)
                    val interactorSettings =
                        Creator.provideSettings(app)
                    SettingsViewModel(
                        interactor,
                        interactorSettings,
                        app
                    )
                }
            }
    }
}