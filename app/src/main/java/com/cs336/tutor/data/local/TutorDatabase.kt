package com.cs336.tutor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cs336.tutor.data.local.dao.ProgressDao
import com.cs336.tutor.data.local.entity.ProgressEntity

@Database(
    entities = [ProgressEntity::class],
    version = 1
)
abstract class TutorDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
}
