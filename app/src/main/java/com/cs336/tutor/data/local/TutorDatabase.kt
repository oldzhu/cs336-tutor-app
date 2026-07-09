package com.cs336.tutor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cs336.tutor.data.local.dao.ChatMessageDao
import com.cs336.tutor.data.local.dao.ProgressDao
import com.cs336.tutor.data.local.entity.ChatMessageEntity
import com.cs336.tutor.data.local.entity.ProgressEntity

@Database(
    entities = [ProgressEntity::class, ChatMessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TutorDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun chatMessageDao(): ChatMessageDao
}
