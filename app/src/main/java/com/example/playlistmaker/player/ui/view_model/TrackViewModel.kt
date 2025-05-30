package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel(
    private val track: Track
) : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private var playerJob: Job? = null


    fun observeState(): LiveData<PlayStatus> = playStatusLiveData

    fun play() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playStatusLiveData.value =
            getCurrentPlayStatus().copy(isPlaying = true, state = STATE_PLAYING)
        startTimer()
    }

    private fun startTimer() {
        playerJob?.cancel()
        playerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                playStatusLiveData.value =
                    getCurrentPlayStatus().copy(progress = mediaPlayer.currentPosition.toFloat())
            }
        }
    }


    fun pause() {
        mediaPlayer.pause()
        playStatusLiveData.value =
            getCurrentPlayStatus().copy(isPlaying = false, state = STATE_PAUSED)
    }

    override fun onCleared() {
        super.onCleared()
        playerJob?.cancel()
        mediaPlayer.release()
        playStatusLiveData.value =
            PlayStatus(progress = 0f, isPlaying = false, state = STATE_DEFAULT)
    }

    fun release() {
        onCleared()
    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl.toString())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playStatusLiveData.value =
                PlayStatus(progress = 0f, isPlaying = false, state = STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerJob?.cancel()
            playStatusLiveData.value =
                PlayStatus(progress = 0f, isPlaying = false, state = STATE_PREPARED)
        }

    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(
            progress = 0f,
            isPlaying = false,
            state = STATE_PREPARED
        )
    }

    fun playbackControl() {


        when (playStatusLiveData.value?.state) {
            STATE_PLAYING -> {
                pause()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                play()

            }
        }
    }
}
