package com.focusguard.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.focusguard.app.data.database.entity.BlockEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockEventDao {

    @Query("SELECT COUNT(*) FROM block_events WHERE dateEpochDay = :epochDay")
    fun getBlockCountForDay(epochDay: Long): Flow<Int>

    @Query("SELECT * FROM block_events WHERE dateEpochDay = :epochDay ORDER BY timestamp DESC")
    fun getBlockEventsForDay(epochDay: Long): Flow<List<BlockEventEntity>>

    @Insert
    suspend fun logBlockEvent(event: BlockEventEntity)
}
