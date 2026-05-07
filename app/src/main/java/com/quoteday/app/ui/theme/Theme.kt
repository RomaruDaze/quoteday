package com.quoteday.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SunnyColorScheme = lightColorScheme(
    primary = Color(0xFFFFB300),
    onPrimary = Color(0xFF1A1208),
    primaryContainer = Color(0xFFFFF9C4),
    onPrimaryContainer = Color(0xFF1A1208),
    secondary = Color(0xFFF57F17),
    onSecondary = Color(0xFF1A1208),
    background = Color(0xFFFFFDE7),
    onBackground = Color(0xFF1A1208),
    surface = Color(0xFFFFFEF0),
    onSurface = Color(0xFF1A1208),
    onSurfaceVariant = Color(0xFF6D5016),
    surfaceVariant = Color(0xFFFFF9C4),
    error = Color(0xFFE53935),
    onError = Color.White,
    outline = Color(0xFFFFD600),
)

@Composable
fun QuoteDayTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(colorScheme = SunnyColorScheme, content = content)
}
