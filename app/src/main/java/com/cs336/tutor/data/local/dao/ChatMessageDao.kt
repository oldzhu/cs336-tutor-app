package com.cs336.tutor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cs336.tutor.data.local.entity.ChatMessageEntity
import com.cs336.tutor.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    
    @Query("SELECT * FROM chat_messages WHERE componentId = :componentId ORDER BY timestamp ASC")
    suspend fun getMessages(componentId: String): List<ChatMessageEntity>
    
    @Query("SELECT * FROM chat_messages WHERE componentId = :componentId ORDER BY timestamp ASC")
    fun observeMessages(componentId: String): Flow<List<ChatMessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)
    
    @Query("DELETE FROM chat_messages WHERE componentId = :componentId")
    suspend fun clearComponent(componentId: String)
    
    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}
