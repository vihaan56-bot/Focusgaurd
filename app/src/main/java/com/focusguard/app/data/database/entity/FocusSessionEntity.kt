package com.focusguard.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val durationMinutes: Int,
    val endTime: Long,
    val isCompleted: Boolean = false,
    val blockedAppsCount: Int = 0
)
