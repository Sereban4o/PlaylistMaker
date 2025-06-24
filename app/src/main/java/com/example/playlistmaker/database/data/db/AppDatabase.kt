package com.example.playlistmaker.database.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.database.data.db.dao.TrackFavoriteDao
import com.example.playlistmaker.database.data.db.entity.TrackFavoriteEntity

@Database(version = 2, entities = [TrackFavoriteEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackFavoriteDao

}