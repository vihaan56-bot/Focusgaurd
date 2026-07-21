package com.focusguard.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "block_events")
data class BlockEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val blockReason: String, // "DAILY_LIMIT", "SESSION_LIMIT", "SCHEDULE", "FOCUS_MODE", "SECTION_REELS", etc.
    val timestamp: Long = System.currentTimeMillis(),
    val dateEpochDay: Long
)
