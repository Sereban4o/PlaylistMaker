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

    @Query("SELECT * FROM playlist_track_table WHERE trackId = :trackId AND playlistId = :playlistId")
    fun checkTrackInPlaylist(trackId: String, playlistId: Int): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_track_table WHERE playlistId = :playlistId ORDER BY id DESC")
    fun getTracks(playlistId: Int): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int)

    @Query("DELETE FROM playlist_track_table WHERE playlistId = :playlistId")
    suspend fun deleteAllTrackFromPlaylist(playlistId: Int)
}