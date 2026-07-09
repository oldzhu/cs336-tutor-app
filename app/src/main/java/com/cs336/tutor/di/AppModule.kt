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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TutorDatabase {
        return Room.databaseBuilder(
            context,
            TutorDatabase::class.java,
            "cs336_tutor.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideProgressDao(database: TutorDatabase): ProgressDao {
        return database.progressDao()
    }

    @Provides
    @Singleton
    fun provideTutorEngine(engine: TutorEngineImpl): TutorEngine = engine

    @Provides
    @Singleton
    fun provideLLMProvider(@ApplicationContext context: Context): LLMProvider {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val providerType = prefs.getString("llm_provider_type", "remote") ?: "remote"
        return if (providerType == "local") {
            val modelPath = prefs.getString("local_model_path", 
                LocalLLMProvider.DEFAULT_MODEL_PATH) ?: LocalLLMProvider.DEFAULT_MODEL_PATH
            val local = LocalLLMProvider()
            local.loadModel(modelPath)
            local
        } else {
            val key = prefs.getString("api_key", "")?.filter { !it.isWhitespace() } ?: ""
            if (key.startsWith("sk-")) {
                DeepSeekLLMProvider(context)
            } else {
                MockLLMProvider()
            }
        }
    }
}
