package com.quoteday.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quoteday.app.R
import com.quoteday.app.ui.theme.LocalAppColors

@Composable
fun SignInScreen(onSignInClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.background),
    ) {
        // Decorative background quote mark
        Text(
            text = """,
            fontSize = 220.sp,
            lineHeight = 180.sp,
            color = colors.accentMustard.copy(alpha = 0.07f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-60).dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "QuoteDay",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "your daily words",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(56.dp))
            OutlinedButton(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.surface,
                    contentColor = colors.textPrimary,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "G",
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}
