package com.cs336.tutor.di

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class TutorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedLocale()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        return context.createConfigurationContext(config)
    }

    private fun applySavedLocale() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
