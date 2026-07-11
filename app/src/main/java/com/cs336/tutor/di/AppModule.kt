package com.cs336.tutor.di

import android.content.Context
import androidx.room.Room
import com.cs336.tutor.data.local.TutorDatabase
import com.cs336.tutor.data.local.dao.ProgressDao
import com.cs336.tutor.data.remote.DeepSeekLLMProvider
import com.cs336.tutor.data.remote.LocalLLMProvider
import com.cs336.tutor.data.remote.MockLLMProvider
import com.cs336.tutor.data.repository.TutorEngineImpl
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.provider.LLMProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton fun provideDatabase(@ApplicationContext c: Context) = Room.databaseBuilder(c, TutorDatabase::class.java, "cs336_tutor.db").fallbackToDestructiveMigration().allowMainThreadQueries().build()
    @Provides @Singleton fun provideProgressDao(db: TutorDatabase) = db.progressDao()
    @Provides @Singleton fun provideChatMessageDao(db: TutorDatabase) = db.chatMessageDao()
    @Provides @Singleton fun provideTutorEngine(e: TutorEngineImpl): TutorEngine = e

    @Provides
    fun provideLLMProvider(@ApplicationContext context: Context): LLMProvider {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val pt = prefs.getString("llm_provider_type", "remote") ?: "remote"
        return if (pt == "local") {
            val path = TutorApplication.modelPath.ifEmpty {
                prefs.getString("local_model_path", "/sdcard/Android/data/com.cs336.tutor/files/model.gguf") ?: ""
            }
            LocalLLMProvider().also { it.loadModel(path) }
        } else {
            val key = prefs.getString("api_key", "")?.filter { !it.isWhitespace() } ?: ""
            if (key.startsWith("sk-")) DeepSeekLLMProvider(context) else MockLLMProvider()
        }
    }
}
