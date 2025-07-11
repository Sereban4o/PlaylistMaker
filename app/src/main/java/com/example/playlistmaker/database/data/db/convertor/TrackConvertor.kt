package com.example.playlistmaker.database.data.db.convertor

import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackConvertor {

    fun map(track: PlaylistTrackEntity): Track {
        return Track(
            track.trackId.toString(),
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