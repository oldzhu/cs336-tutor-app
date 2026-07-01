package com.cs336.tutor.ui

import android.content.Context
import android.content.res.Configuration
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

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
        val config = Configuration(newBase.resources.configuration).apply {
            setLocale(locale)
        }
        @Suppress("DEPRECATION")
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun getResources(): android.content.res.Resources {
        val res = super.getResources()
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH
        val config = Configuration(res.configuration).apply { setLocale(locale) }
        @Suppress("DEPRECATION")
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }
}
