package com.quoteday.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quoteday.app.R
import com.quoteday.app.data.Quote
import com.quoteday.app.ui.theme.LocalAppColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayQuoteScreen(quote: Quote?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val dateText = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelLarge,
            color = colors.textMuted,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(16.dp))

        Crossfade(
            targetState = quote,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            animationSpec = tween(400),
            label = "quoteContent",
        ) { currentQuote ->
            if (currentQuote == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\u201C",
                            fontSize = 64.sp,
                            color = colors.accentMustard.copy(alpha = 0.35f),
                            fontWeight = FontWeight.Light,
                            lineHeight = 56.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No quotes yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textSecondary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "add a quote to see today's pick",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            } else {
                OutlinedCard(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val maxTextHeight = maxHeight * 0.6f
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_quote),
                                contentDescription = null,
                                tint = colors.accentMustard.copy(alpha = 0.4f),
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            AutoSizeText(
                                text = currentQuote.text,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = maxTextHeight),
                                color = colors.textPrimary,
                            )
                            if (currentQuote.author.isNotBlank()) {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "— ${currentQuote.author}",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 14.sp,
                                    color = colors.accentWarm,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    minFontSize: Float = 14f,
    maxFontSize: Float = 72f,
) {
    var fontSize by remember(text) { mutableFloatStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        color = color,
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.35f).sp,
        overflow = TextOverflow.Clip,
        softWrap = true,
        onTextLayout = { result ->
            if ((result.didOverflowHeight || result.didOverflowWidth) && fontSize > minFontSize) {
                fontSize = (fontSize * 0.85f).coerceAtLeast(minFontSize)
            } else {
                readyToDraw = true
            }
        },
    )
}
