package com.cs336.tutor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val componentId: String,
    val role: String,      // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
