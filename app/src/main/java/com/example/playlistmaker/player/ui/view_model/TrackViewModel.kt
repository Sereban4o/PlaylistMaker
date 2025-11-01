package com.example.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.services.AudioState
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.services.AudioPlayerControl
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleEventLiveData
import kotlinx.coroutines.launch

class TrackViewModel(
    private val track: Track,
    application: Application,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
    ) : AndroidViewModel(application) {

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    private val message = SingleEventLiveData<String>()
    private val isAddedState = SingleEventLiveData<Boolean>()
    private val audioState = MutableLiveData<AudioState>(AudioState.Default())
    fun observeAudioState(): LiveData<AudioState> = audioState
    private var audioPlayerControl: AudioPlayerControl? = null

    init {
        viewModelScope.launch {
            checkFavorite(track)
        }
        message.value = ""
        isAddedState.value = false
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                audioState.postValue(it)
            }
        }
    }

    fun showNotification() {
        audioPlayerControl?.showNotification()
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

    fun onPlayerButtonClicked() {
        if (audioState.value is AudioState.Playing) {
            audioPlayerControl?.pausePlayer()
        } else {
            audioPlayerControl?.startPlayer()
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
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

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(
            isFavorite = false,
            playlists = mutableListOf()
        )
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

    fun addToPlaylist(playlist: Playlist, track: Track ) {
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
