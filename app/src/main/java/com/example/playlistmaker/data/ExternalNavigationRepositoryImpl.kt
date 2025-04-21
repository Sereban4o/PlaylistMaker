package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.ExternalNavigationInteractor

class ExternalNavigationRepositoryImpl : ExternalNavigationInteractor{
    override fun share(context: Context) {
        val intent = Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.addressPracticum)
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun mail(context: Context) {
        val message = context.getString(R.string.mailMessage)
        val title = context.getString(R.string.mailTitle)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mailAddress)))
        intent.putExtra(Intent.EXTRA_TEXT, title)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openUrl(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            context.getString(R.string.offer).toUri()
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}