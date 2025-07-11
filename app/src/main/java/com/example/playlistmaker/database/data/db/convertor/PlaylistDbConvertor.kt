package com.example.playlistmaker.database.data.db.convertor

import com.example.playlistmaker.database.data.db.entity.PlaylistEntity
import com.example.playlistmaker.database.data.db.model.PlaylistWithCount
import com.example.playlistmaker.playlists.domain.model.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.note,
            playlist.imageUri
        )
    }

    fun map(playlist: PlaylistWithCount): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.note,
            playlist.imageUri,
            playlist.countTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.note,
            playlist.imageUri

        )
    }
}