package com.quoteday.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quoteday.app.ui.theme.LocalAppColors

@Composable
fun SignInScreen(onSignInClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "QuoteDay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "your daily words",
                fontSize = 13.sp,
                color = colors.textSecondary,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onSignInClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign in with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
