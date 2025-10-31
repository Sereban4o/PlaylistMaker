package com.example.playlistmaker.player.services

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<AudioState>
    fun startPlayer()
    fun pausePlayer()
}