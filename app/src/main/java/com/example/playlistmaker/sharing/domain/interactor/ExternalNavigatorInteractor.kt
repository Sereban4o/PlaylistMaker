package com.example.playlistmaker.sharing.domain.interactor

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigatorInteractor {
    fun shareLink(url: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}