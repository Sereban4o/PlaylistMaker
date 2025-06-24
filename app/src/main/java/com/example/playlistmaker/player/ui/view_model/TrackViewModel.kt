package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel(
    private val track: Track,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

    private var mediaPlayer = MediaPlayer()
    private val playerStateLiveData = MutableLiveData<PlayerState>()
    private var playerJob: Job? = null
    private var inFavorite: Boolean = false

    init {
        viewModelScope.launch {
            checkFavorite(track)
        }
    }

    fun observeState(): LiveData<PlayerState> = playerStateLiveData

    private fun play() {
        mediaPlayer.start()
        playerStateLiveData.value =
            getCurrentPlayerState().copy(isPlaying = true, state = STATE_PLAYING)
        startTimer()
    }

    private fun startTimer() {
        playerJob?.cancel()
        playerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                playerStateLiveData.value =
                    getCurrentPlayerState().copy(progress = mediaPlayer.currentPosition.toFloat())
            }
        }
    }

    private fun pause() {
        mediaPlayer.pause()
        playerStateLiveData.value =
            getCurrentPlayerState().copy(isPlaying = false, state = STATE_PAUSED)
    }

    override fun onCleared() {
        super.onCleared()
        playerJob?.cancel()
        mediaPlayer.release()
        playerStateLiveData.value =
            PlayerState(
                progress = 0f,
                isPlaying = false,
                state = STATE_DEFAULT,
                inFavorite = inFavorite
            )
    }

    fun release() {
        onCleared()
    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl.toString())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value =
                PlayerState(
                    progress = 0f,
                    isPlaying = false,
                    state = STATE_PREPARED,
                    inFavorite = inFavorite
                )
        }
        mediaPlayer.setOnCompletionListener {
            playerJob?.cancel()
            playerStateLiveData.value =
                PlayerState(
                    progress = 0f,
                    isPlaying = false,
                    state = STATE_PREPARED,
                    inFavorite = inFavorite
                )
        }

    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(
            progress = 0f,
            isPlaying = false,
            state = STATE_PREPARED, inFavorite = inFavorite
        )
    }

    fun playbackControl() {
        when (playerStateLiveData.value?.state) {
            STATE_PLAYING -> {
                pause()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                play()
            }
        }
    }

    fun editFavorite(track: Track) {
        viewModelScope.launch {
            if (inFavorite) {
                deleteFavoriteSuspend(track)
            } else {
                addFavoriteSuspend(track)
            }
            checkFavorite(track)
        }
    }

    private suspend fun addFavoriteSuspend(track: Track) {
        favoritesInteractor.addFavorite(track)
    }

    private suspend fun deleteFavoriteSuspend(track: Track) {
        track.trackId?.let { favoritesInteractor.deleteFavorite(it) }
    }

    private suspend fun checkFavorite(track: Track) {
        inFavorite = track.trackId?.let {
            favoritesInteractor.checkFavorite(it)
        } == true
        playerStateLiveData.value =
            getCurrentPlayerState().copy(inFavorite = inFavorite)
    }
}
