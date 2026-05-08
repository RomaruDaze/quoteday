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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SignInBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFF3A0), Color(0xFFFFDE59)),
    startY = 0f, endY = 4000f
)
private val SignInSurface  = Color(0xFFFFFCF0)
private val SignInBorder   = Color(0xFFE8C830)
private val SignInCharcoal = Color(0xFF3A2E18)
private val SignInTextPrimary   = Color(0xFF1C1910)
private val SignInTextSecondary = Color(0xFF6B5E38)

@Composable
fun SignInScreen(onSignInClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = SignInBackground),
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
                color = SignInTextPrimary,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "your daily words",
                fontSize = 13.sp,
                color = SignInTextSecondary,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SignInSurface)
                    .border(1.dp, SignInBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onSignInClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign in with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SignInCharcoal,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
