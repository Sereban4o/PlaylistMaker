package com.example.playlistmaker.sharing.data.impl

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.sharing.domain.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val app: Application) : ExternalNavigatorInteractor {

    override fun shareLink(url: String) {
        val intent = Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
            Intent.EXTRA_TEXT,
            url.toUri()
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
    }

    override fun openLink(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, emailData.emailAddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        intent.putExtra(Intent.EXTRA_TEXT, emailData.messageBody)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
    }
}