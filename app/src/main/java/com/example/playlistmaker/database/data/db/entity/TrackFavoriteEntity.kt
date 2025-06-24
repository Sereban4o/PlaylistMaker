package com.example.playlistmaker.database.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_favorite_table")
data class TrackFavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
){
    constructor(
        trackId: String,
        trackName: String,
        artistName: String,
        trackTimeMillis: String,
        artworkUrl100: String,
        collectionName: String,
        releaseDate: String,
        primaryGenreName: String,
        country: String,
        previewUrl: String
    ): this(null, trackId, trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
}