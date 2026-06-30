package com.cs336.tutor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class ProgressEntity(
    @PrimaryKey
    val componentId: String,
    val completedLines: Int,
    val totalLines: Int,
    val isCompleted: Boolean = false,
    val lastScore: Float = 0f,
    val lastPracticedAt: Long = System.currentTimeMillis()
)
