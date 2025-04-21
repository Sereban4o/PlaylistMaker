package com.example.playlistmaker.domain

import android.content.Context

interface ExternalNavigationInteractor {
    fun share(context: Context)

    fun mail(context: Context)

    fun openUrl(context: Context)
}