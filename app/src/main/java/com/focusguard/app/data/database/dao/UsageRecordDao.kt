package com.focusguard.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusguard.app.data.database.entity.UsageRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageRecordDao {

    @Query("SELECT * FROM usage_records WHERE dateEpochDay = :epochDay ORDER BY totalTimeInForegroundMs DESC")
    fun getRecordsForDay(epochDay: Long): Flow<List<UsageRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecords(records: List<UsageRecordEntity>)

    @Query("DELETE FROM usage_records WHERE dateEpochDay < :beforeEpochDay")
    suspend fun deleteOldRecords(beforeEpochDay: Long)
}
