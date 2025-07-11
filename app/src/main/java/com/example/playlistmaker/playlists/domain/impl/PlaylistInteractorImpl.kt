package com.example.playlistmaker.playlists.domain.impl

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.playlists.domain.interactor.PlaylistInteractor

class PlaylistInteractorImpl(private val app: Application) : PlaylistInteractor {
    override fun sharePlaylist(
        text: String,
    ) {

        val intent = Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
            Intent.EXTRA_TEXT,
            text
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
    }
}