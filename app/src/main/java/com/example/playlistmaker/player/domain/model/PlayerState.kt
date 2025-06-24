package com.example.playlistmaker.player.domain.model

data class PlayerState(val progress: Float, var isPlaying: Boolean, val state: Int, val inFavorite: Boolean) {

}