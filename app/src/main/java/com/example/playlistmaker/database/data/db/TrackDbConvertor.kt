package com.example.playlistmaker.database.data.db

import com.example.playlistmaker.database.data.db.entity.TrackFavoriteEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackFavoriteEntity {
        return TrackFavoriteEntity(
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

    fun map(track: TrackFavoriteEntity): Track {
        return Track(
            track.trackId,
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


}