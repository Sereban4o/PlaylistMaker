import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.database.data.db.entity.PlaylistEntity
import com.example.playlistmaker.database.data.db.model.PlaylistWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(track: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query(
        "SELECT " +
                "COUNT(playlist_track_table.id) as countTracks, " +
                "playlist_table.id, " +
                "playlist_table.name, " +
                "playlist_table.imageUri, " +
                "playlist_table.note " +
                "FROM playlist_table " +
                "LEFT JOIN playlist_track_table " +
                "ON playlist_table.id = playlist_track_table.playlistId " +
                "GROUP BY " +
                "playlist_table.id, " +
                "playlist_table.name, " +
                "playlist_table.imageUri, " +
                "playlist_table.note"
    )
    fun getPlaylists(): Flow<List<PlaylistWithCount>>

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistById(id: Int): Flow<List<PlaylistEntity>>
}