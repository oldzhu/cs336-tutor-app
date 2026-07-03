package com.cs336.tutor.di

import android.content.Context
import androidx.room.Room
import com.cs336.tutor.data.local.ProgressDao
import com.cs336.tutor.data.local.TutorDatabase
import com.cs336.tutor.data.remote.HermesLLMProvider
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
        return Room.databaseBuilder(context, TutorDatabase::class.java, "cs336_tutor.db").build()
    }

    @Provides @Singleton
    fun provideProgressDao(database: TutorDatabase): ProgressDao = database.progressDao()

    @Provides @Singleton
    fun provideTutorEngine(engine: TutorEngineImpl): TutorEngine = engine

    @Provides @Singleton
    fun provideLLMProvider(@ApplicationContext context: Context): LLMProvider {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val hasKey = (prefs.getString("api_key", "") ?: "").isNotEmpty()
        return if (hasKey) HermesLLMProvider(context) else MockLLMProvider()
    }
}
