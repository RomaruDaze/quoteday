package com.quoteday.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JapandiColorScheme = lightColorScheme(
    primary = Color(0xFF6B5E52),
    onPrimary = Color(0xFFFAF8F5),
    primaryContainer = Color(0xFFEDE8E2),
    onPrimaryContainer = Color(0xFF1C1A18),
    secondary = Color(0xFF7A9080),
    onSecondary = Color(0xFFFAF8F5),
    background = Color(0xFFF5F0EB),
    onBackground = Color(0xFF1C1A18),
    surface = Color(0xFFFAF8F5),
    onSurface = Color(0xFF1C1A18),
    onSurfaceVariant = Color(0xFF6B6560),
    surfaceVariant = Color(0xFFEDE8E2),
    error = Color(0xFFC0392B),
    onError = Color.White,
    outline = Color(0xFFDDD8D2),
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

    MaterialTheme(colorScheme = JapandiColorScheme, content = content)
}
