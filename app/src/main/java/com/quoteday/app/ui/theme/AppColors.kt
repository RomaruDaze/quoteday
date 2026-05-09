package com.quoteday.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AppColors(
    val background: Brush,
    val surface: Color,
    val overlay: Color,
    val cardBorder: Color,
    val accentMustard: Color,
    val accentWarm: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val deleteRed: Color,
    val buttonBackground: Color,
    val buttonContent: Color,
)

val LightAppColors = AppColors(
    background = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFF3A0), Color(0xFFFFDE59)),
        startY = 0f, endY = 4000f,
    ),
    surface          = Color(0xFFFFFCF0),
    overlay          = Color(0xFF3A2E18),
    cardBorder       = Color(0xFFE8C830),
    accentMustard    = Color(0xFFB5892A),
    accentWarm       = Color(0xFF7A6A45),
    textPrimary      = Color(0xFF1C1910),
    textSecondary    = Color(0xFF6B5E38),
    textMuted        = Color(0xFFA09068),
    deleteRed        = Color(0xFFC0392B),
    buttonBackground = Color(0xFF3A2E18),
    buttonContent    = Color(0xFFFFFCF0),
)

val DarkAppColors = AppColors(
    background = Brush.verticalGradient(
        colors = listOf(Color(0xFF1C1910), Color(0xFF241A0C), Color(0xFF2E2210)),
        startY = 0f, endY = 4000f,
    ),
    surface          = Color(0xFF2A1F0E),
    overlay          = Color(0xFF100C06),
    cardBorder       = Color(0xFF5A4826),
    accentMustard    = Color(0xFFCA9B38),
    accentWarm       = Color(0xFF9A7D50),
    textPrimary      = Color(0xFFFCF8EC),
    textSecondary    = Color(0xFFC4A97A),
    textMuted        = Color(0xFF7A6545),
    deleteRed        = Color(0xFFE05040),
    buttonBackground = Color(0xFFB5892A),
    buttonContent    = Color(0xFF1C1910),
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
