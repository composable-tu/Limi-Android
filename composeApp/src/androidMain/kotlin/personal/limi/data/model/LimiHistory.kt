package personal.limi.data.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LimiHistoryDao {
    @Insert
    suspend fun insert(item: LimiHistoryEntity)

    @Query("SELECT count(*) FROM LimiHistoryEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM LimiHistoryEntity")
    fun getAllAsFlow(): Flow<List<LimiHistoryEntity>>
}

@Entity
data class LimiHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originUrl: String,
    val originHost: String,
    val processedUrl: String,
    val datetime: String
)