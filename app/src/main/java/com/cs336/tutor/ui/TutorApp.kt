package com.cs336.tutor.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs336.tutor.ui.screens.DashboardScreen
import com.cs336.tutor.ui.screens.SplitScreenTutorScreen
import com.cs336.tutor.ui.screens.SettingsScreen

@Composable
fun TutorApp() {
    val navController = rememberNavController()

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
