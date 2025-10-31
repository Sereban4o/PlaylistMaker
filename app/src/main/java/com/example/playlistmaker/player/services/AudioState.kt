package com.example.playlistmaker.player.services

sealed class AudioState(
    val isPlaying: Boolean, val progress: String
) {
    class Default : AudioState(false, "00:00")

    class Prepared : AudioState(false, "00:00")

    class Playing(progress: String) : AudioState(true, progress)

    class Paused(progress: String) : AudioState(false, progress)
}