package com.cs336.tutor.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cs336.tutor.ui.theme.CS336TutorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        enableEdgeToEdge()
        setContent {
            CS336TutorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorApp()
                }
            }
        }
    }

    private fun applySavedLocale() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    companion object {
        fun applyLocale(context: Context) {
            val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val lang = prefs.getString("language", "en") ?: "en"
            val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
            Locale.setDefault(locale)
        }
    }
}
