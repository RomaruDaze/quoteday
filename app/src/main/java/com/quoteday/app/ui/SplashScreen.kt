package com.quoteday.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quoteday.app.BuildConfig
import com.quoteday.app.R
import com.quoteday.app.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val colors = LocalAppColors.current

    val screenAlpha  = remember { Animatable(1f) }
    val logoAlpha    = remember { Animatable(0f) }
    val logoOffsetY  = remember { Animatable(24f) }
    val titleAlpha   = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        val enterSpec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)

        // Logo slides up and fades in
        launch { logoAlpha.animateTo(1f, enterSpec) }
        launch { logoOffsetY.animateTo(0f, enterSpec) }

        // Title follows 150 ms later
        kotlinx.coroutines.delay(150)
        launch { titleAlpha.animateTo(1f, enterSpec) }
        launch { titleOffsetY.animateTo(0f, enterSpec) }

        // Hold, then fade the whole screen out
        kotlinx.coroutines.delay(600)
        screenAlpha.animateTo(0f, tween(durationMillis = 220))
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenAlpha.value)
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .alpha(logoAlpha.value)
                    .offset(y = logoOffsetY.value.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "QuoteDay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffsetY.value.dp),
            )
        }

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(titleAlpha.value),
            fontSize = 11.sp,
            color = colors.textSecondary,
            letterSpacing = 1.sp,
        )
    }
}
