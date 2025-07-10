package com.example.playlistmaker.playlists.ui.view_model

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.state.PlaylistState
import com.example.playlistmaker.playlists.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val application: Application,
    private val playlist: Playlist,
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : AndroidViewModel(application) {

    private var allTime = 0L
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): MutableLiveData<PlaylistState> = stateLiveData

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlist.id).collect { playlist ->
                for (el in playlist) {
                    stateLiveData.value = getCurrentState().copy(
                        playlist = el
                    )
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun fillData() {
        viewModelScope.launch {

            playlistsInteractor.getTracks(playlist.id).collect { tracks ->
                allTime = 0L
                val formatter = SimpleDateFormat("mm:ss")
                for (track in tracks) {
                    allTime += formatter.parse(track.trackTimeMillis).time
                }
                val allTimeText = SimpleDateFormat("mm:ss", Locale.getDefault()).format(allTime)
                stateLiveData.value = getCurrentState().copy(
                    trackList = tracks,
                    count = tracks.size,
                    allTime = allTimeText
                )
            }
        }
    }

    private fun getCurrentState(): PlaylistState {
        return stateLiveData.value ?: PlaylistState(
            playlist = playlist,
            trackList = emptyList(),
            count = 0,
            allTime = ""
        )
    }

    fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
        }
    }


    fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {
        var text = playlist.name + "\n"
        text += playlist.note + "\n"
        text += getApplication<Application>().resources.getQuantityString(
            R.plurals.tracks,
            trackList.size,
            trackList.size
        ) + "\n"

        for (track in trackList) {
            text += getApplication<Application>().getString(
                R.string.sharingTrack,
                (trackList.indexOf(track) + 1).toString(),
                track.artistName,
                track.trackName,
                track.trackTimeMillis
            ) + "\n"
        }
        playlistInteractor.sharePlaylist(text)
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
        }
    }
}