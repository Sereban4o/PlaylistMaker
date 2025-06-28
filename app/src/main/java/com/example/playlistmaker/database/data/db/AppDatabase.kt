package com.example.playlistmaker.database.data.db

import PlaylistDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.database.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.database.data.db.dao.TrackFavoriteDao
import com.example.playlistmaker.database.data.db.entity.TrackFavoriteEntity
import com.example.playlistmaker.database.data.db.entity.PlaylistEntity
import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity

@Database(
    version = 1,
    entities = [TrackFavoriteEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackFavoriteDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}