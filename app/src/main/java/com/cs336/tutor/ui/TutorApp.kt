package com.cs336.tutor.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs336.tutor.ui.screens.DashboardScreen
import com.cs336.tutor.ui.screens.SplitScreenTutorScreen
import com.cs336.tutor.ui.screens.SettingsScreen
import java.util.Locale

@Composable
fun TutorApp() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val lang = prefs.getString("language", "en") ?: "en"
    val locale = if (lang == "zh") Locale.SIMPLIFIED_CHINESE else Locale.ENGLISH

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    val localeContext = context.createConfigurationContext(config)

    val navController = rememberNavController()

    CompositionLocalProvider(LocalContext provides localeContext) {
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    onNavigateToTutor = { componentId ->
                        navController.navigate("tutor/$componentId")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("tutor/{componentId}") { backStackEntry ->
                val componentId = backStackEntry.arguments?.getString("componentId") ?: "bpe"
                SplitScreenTutorScreen(
                    componentId = componentId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
