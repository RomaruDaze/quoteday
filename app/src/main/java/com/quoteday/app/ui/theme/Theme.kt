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
    primary = Color(0xFFB5892A),
    onPrimary = Color(0xFFFCF8EC),
    primaryContainer = Color(0xFFEEDFA8),
    onPrimaryContainer = Color(0xFF1C1910),
    secondary = Color(0xFF7A6A45),
    onSecondary = Color(0xFFFCF8EC),
    background = Color(0xFFF5EDD0),
    onBackground = Color(0xFF1C1910),
    surface = Color(0xFFFCF8EC),
    onSurface = Color(0xFF1C1910),
    onSurfaceVariant = Color(0xFF6B5E38),
    surfaceVariant = Color(0xFFEEDFA8),
    error = Color(0xFFC0392B),
    onError = Color.White,
    outline = Color(0xFFE0D4A8),
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
