package com.example.playlistmaker.database.data.db.convertor

import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.playlists.domain.model.PlaylistTrack
import com.example.playlistmaker.search.domain.models.Track

class PlaylistTrackDbConvertor {
    fun map(track: PlaylistTrack): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.playlistId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(track: PlaylistTrackEntity): PlaylistTrack {
        return PlaylistTrack(
            track.trackId,
            track.playlistId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(track: Track, playlistId: Int): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId.toString(),
            playlistId,
            track.trackName.toString(),
            track.artistName.toString(),
            track.trackTimeMillis.toString(),
            track.artworkUrl100.toString(),
            track.collectionName.toString(),
            track.releaseDate.toString(),
            track.primaryGenreName.toString(),
            track.country.toString(),
            track.previewUrl.toString()
        )
    }
}