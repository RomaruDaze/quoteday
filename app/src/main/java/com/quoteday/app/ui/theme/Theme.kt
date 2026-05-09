package com.quoteday.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JapandiLightScheme = lightColorScheme(
    primary              = Color(0xFFB5892A),
    onPrimary            = Color(0xFFFCF8EC),
    primaryContainer     = Color(0xFFEEDFA8),
    onPrimaryContainer   = Color(0xFF1C1910),
    secondary            = Color(0xFF7A6A45),
    onSecondary          = Color(0xFFFCF8EC),
    background           = Color(0xFFFFDE59),
    onBackground         = Color(0xFF1C1910),
    surface              = Color(0xFFFFFCF0),
    onSurface            = Color(0xFF1C1910),
    onSurfaceVariant     = Color(0xFF6B5E38),
    surfaceVariant       = Color(0xFFEEDFA8),
    error                = Color(0xFFC0392B),
    onError              = Color.White,
    outline              = Color(0xFFE0D4A8),
)

private val JapandiDarkScheme = darkColorScheme(
    primary              = Color(0xFFCA9B38),
    onPrimary            = Color(0xFF1C1910),
    primaryContainer     = Color(0xFF3A2E18),
    onPrimaryContainer   = Color(0xFFFCF8EC),
    secondary            = Color(0xFF9A7D50),
    onSecondary          = Color(0xFF1C1910),
    background           = Color(0xFF1C1910),
    onBackground         = Color(0xFFFCF8EC),
    surface              = Color(0xFF2A1F0E),
    onSurface            = Color(0xFFFCF8EC),
    onSurfaceVariant     = Color(0xFFC4A97A),
    surfaceVariant       = Color(0xFF3A2E18),
    error                = Color(0xFFE05040),
    onError              = Color.White,
    outline              = Color(0xFF5A4826),
)

@Composable
fun QuoteDayTheme(content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val appColors = if (isDark) DarkAppColors else LightAppColors
    val colorScheme = if (isDark) JapandiDarkScheme else JapandiLightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
