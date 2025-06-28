package com.example.playlistmaker.database.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.database.data.db.entity.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFavoriteDao {
    @Insert(entity = TrackFavoriteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(track: TrackFavoriteEntity)

    @Query("SELECT * FROM track_favorite_table ORDER BY id DESC")
    fun getFavorites(): Flow<List<TrackFavoriteEntity>>

    @Query("SELECT * FROM track_favorite_table WHERE trackId = :trackId")
    suspend fun checkFavorite(trackId: String): List<TrackFavoriteEntity>

    @Query("DELETE FROM track_favorite_table WHERE trackId = :trackId")
    suspend fun deleteFavorite(trackId: String)
}