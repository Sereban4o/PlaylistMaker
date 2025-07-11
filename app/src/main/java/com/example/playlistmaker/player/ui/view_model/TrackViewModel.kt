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
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleEventLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel(
    private val track: Track,
    application: Application,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val mediaPlayer: MediaPlayer
) : AndroidViewModel(application) {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    private val message = SingleEventLiveData<String>()
    private val isAddedState = SingleEventLiveData<Boolean>()
    private var playerJob: Job? = null

    init {
        viewModelScope.launch {
            checkFavorite(track)
        }
        message.value = ""
        isAddedState.value = false
    }

    private fun processResult(result: List<Playlist>) {
        if (result.isNotEmpty()) {
            playerStateLiveData.value =
                getCurrentPlayerState().copy(playlists = result)
        }
    }

    fun observeState(): LiveData<PlayerState> = playerStateLiveData
    fun observeMessage(): LiveData<String> = message
    fun observeIsAdded(): LiveData<Boolean> = isAddedState

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
            playlists = mutableListOf()
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

    fun getPlaylists() {
        viewModelScope.launch {
            getPlaylistsSuspend()
        }
    }

    private suspend fun getPlaylistsSuspend() {
        message.value = ""
        isAddedState.value = false
        playlistsInteractor.getPlaylists().collect { result ->
            processResult(result)
        }
    }

    fun addToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            var isAdded = false
            playlistsInteractor.checkTrackInPlaylist(track.trackId.toString(), playlist.id)
                .collect { result ->
                    if (result.isNotEmpty()) {
                        isAdded = true
                        message.value = getApplication<Application>().getString(
                            R.string.messageAlreadyAdded,
                            playlist.name
                        )
                    }
                    if (!isAdded) {
                        addTrackToPlaylist(track, playlist.id)
                        message.value = getApplication<Application>().getString(
                            R.string.messageAdded, playlist.name
                        )
                        getPlaylistsSuspend()
                    }
                }
            isAddedState.value = isAdded
        }
    }

    private suspend fun addTrackToPlaylist(track: Track, playlistId: Int) {
        playlistsInteractor.addTrackToPlaylist(track, playlistId)
        message.value = ""
        isAddedState.value = false
    }
}
