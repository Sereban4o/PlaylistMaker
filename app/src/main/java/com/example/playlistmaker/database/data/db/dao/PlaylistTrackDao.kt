package com.example.playlistmaker.database.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.database.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table")
    fun getTracks(): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_track_table WHERE trackId = :trackId AND playlistId = :playlistId")
    fun checkTrackInPlaylist(trackId: String, playlistId: Int): Flow<List<PlaylistTrackEntity>>
}