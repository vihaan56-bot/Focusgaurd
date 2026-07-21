package com.focusguard.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_records")
data class UsageRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val totalTimeInForegroundMs: Long,
    val lastTimeUsed: Long
)
