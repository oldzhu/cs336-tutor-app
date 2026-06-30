package com.cs336.tutor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colors
val PrimaryColor = Color(0xFF4F46E5)        // Indigo
val OnPrimaryColor = Color(0xFFFFFFFF)
val SecondaryColor = Color(0xFF7C3AED)       // Violet
val BackgroundColor = Color(0xFF0F0F1A)      // Dark background
val SurfaceColor = Color(0xFF1A1A2E)         // Dark surface
val LightBackgroundColor = Color(0xFFF8F9FC)
val LightSurfaceColor = Color(0xFFFFFFFF)

// Typography
val Typography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp
    )
)
