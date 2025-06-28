package com.example.playlistmaker.player.ui.view_model

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.playlists.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel(
    private val track: Track,
    application: Application,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

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

    init {
        viewModelScope.launch {
            checkFavorite(track)
            playlistInteractor.getPlaylists().collect { result ->
                processResult(result)
            }
        }
    }

    private fun processResult(result: List<Playlist>) {
        if (result.isNotEmpty()) {
            playerStateLiveData.value =
                getCurrentPlayerState().copy(playlists = result)
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
            getCurrentPlayerState().copy(progress = 0f, isPlaying = false, state = STATE_DEFAULT)
    }

    fun release() {
        onCleared()
    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl.toString())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value =
                getCurrentPlayerState().copy(
                    progress = 0f,
                    isPlaying = false,
                    state = STATE_PREPARED
                )
        }
        mediaPlayer.setOnCompletionListener {
            playerJob?.cancel()
            playerStateLiveData.value =
                getCurrentPlayerState().copy(
                    progress = 0f,
                    isPlaying = false,
                    state = STATE_PREPARED
                )
        }
    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(
            progress = 0f,
            isPlaying = false,
            state = STATE_PREPARED, isFavorite = false,
            playlists = mutableListOf(),
            isAdded = false,
            message = ""
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
            if (playerStateLiveData.value?.isFavorite == true) {
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
        val isFavorite = track.trackId?.let {
            favoritesInteractor.checkFavorite(it)
        } == true
        playerStateLiveData.value =
            getCurrentPlayerState().copy(isFavorite = isFavorite)
    }

    fun addToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            checkTrackInPlaylist(playlist)
        }
    }

    private suspend fun checkTrackInPlaylist(playlist: Playlist) {
        playlistInteractor.checkTrackInPlaylist(track.trackId.toString(), playlist.id)
            .collect { result ->
                if (result.isNotEmpty()) {
                    playerStateLiveData.value =
                        getCurrentPlayerState().copy(
                            message = getApplication<Application>().getString(
                                R.string.messageAlreadyAdded,
                                playlist.name
                            )
                        )
                } else {
                    addTrackToPlaylist(track, playlist.id)

                    playerStateLiveData.value =
                        getCurrentPlayerState().copy(
                            isAdded = true,
                            message = getApplication<Application>().getString(
                                R.string.messageAdded,
                                playlist.name
                            )
                        )

                }
                playlistInteractor.getPlaylists().collect { result ->
                    processResult(result)
                }
            }
    }

    private suspend fun addTrackToPlaylist(track: Track, playlistId: Int) {
        playlistInteractor.addTrackToPlaylist(track, playlistId)
    }

    fun clearAdd() {
        playerStateLiveData.value =
            getCurrentPlayerState().copy(isAdded = false, message = "")
    }
}
