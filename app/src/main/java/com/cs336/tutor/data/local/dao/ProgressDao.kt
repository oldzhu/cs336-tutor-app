package com.cs336.tutor.data.local.dao

import androidx.room.*
import com.cs336.tutor.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE componentId = :componentId")
    fun getProgress(componentId: String): Flow<ProgressEntity?>

    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity)

    @Query("DELETE FROM user_progress WHERE componentId = :componentId")
    suspend fun delete(componentId: String)
}
