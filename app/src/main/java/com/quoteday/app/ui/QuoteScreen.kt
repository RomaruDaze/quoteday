package com.quoteday.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.quoteday.app.data.Quote

private val ScreenBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFDE7),
        Color(0xFFFFF9C4),
        Color(0xFFFFF59D),
    )
)

private val CardFill = Color(0xFFFFFEF0)
private val CardBorder = Color(0xFFFFD600).copy(alpha = 0.50f)
private val CardShadow = Color(0xFFFFCC00).copy(alpha = 0.20f)
private val AccentAmber = Color(0xFFFFB300)
private val AccentAmberDark = Color(0xFFF57F17)
private val TextPrimary = Color(0xFF1A1208)
private val TextSecondary = Color(0xFF6D5016)
private val TextMuted = Color(0xFFAD8B2B)
private val DeleteRed = Color(0xFFE53935)

@Composable
fun QuoteScreen(viewModel: QuoteViewModel) {
    val quotes by viewModel.quotes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingQuote by remember { mutableStateOf<Quote?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = ScreenBackground)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            topBar = { SunnyHeader() },
            floatingActionButton = {
                SunnyFab(onClick = { showDialog = true })
            }
        ) { padding ->
            if (quotes.isEmpty()) {
                SunnyEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quotes, key = { it.id }) { quote ->
                        SunnyQuoteItem(
                            quote = quote,
                            onClick = { editingQuote = quote }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        SunnyAddQuoteDialog(
            onConfirm = { text, author ->
                viewModel.addQuote(text, author)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    editingQuote?.let { quote ->
        SunnyEditQuoteDialog(
            quote = quote,
            onSave = { updated ->
                viewModel.updateQuote(updated)
                editingQuote = null
            },
            onDelete = {
                viewModel.deleteQuote(quote)
                editingQuote = null
            },
            onDismiss = { editingQuote = null }
        )
    }
}

@Composable
private fun SunnyHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF176),
                        Color(0xFFFFF9C4),
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFFFFD600).copy(alpha = 0.40f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "QuoteDay ☀️",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Your daily inspiration",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                letterSpacing = 0.2.sp,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(Color(0xFFFFD600).copy(alpha = 0.35f))
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SunnyQuoteItem(quote: Quote, onClick: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = CardShadow,
                spotColor = CardShadow,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardFill)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFFFD600).copy(alpha = 0.70f),
                            Color(0xFFFFD600).copy(alpha = 0.70f),
                            Color.Transparent,
                        )
                    )
                )
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 20.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(36.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AccentAmber)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = quote.text,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextPrimary,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp,
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (quote.author.isNotBlank()) {
                    Text(
                        text = "— ${quote.author}",
                        fontSize = 12.sp,
                        color = AccentAmberDark,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .clickable {
                        val copyText = if (quote.author.isNotBlank())
                            "${quote.text} — ${quote.author}"
                        else
                            quote.text
                        clipboard.setText(AnnotatedString(copyText))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SunnyEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD600).copy(alpha = 0.25f),
                            Color.Transparent,
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = CardBorder,
                    shape = CircleShape
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "☀️",
                fontSize = 48.sp,
                lineHeight = 52.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No quotes yet.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap + to add your first one.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SunnyFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 24.dp, end = 8.dp)
            .size(60.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = AccentAmber.copy(alpha = 0.40f),
                spotColor = AccentAmber.copy(alpha = 0.40f),
            )
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD600),
                        Color(0xFFFFB300),
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add quote",
            tint = Color(0xFF1A1208),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun SunnyAddQuoteDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val canConfirm = text.isNotBlank()

    val sunnyFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AccentAmber,
        focusedBorderColor = AccentAmber,
        unfocusedBorderColor = Color(0xFFFFD600).copy(alpha = 0.60f),
        focusedLabelColor = AccentAmberDark,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = Color(0xFFFFFDE7),
        unfocusedContainerColor = Color(0xFFFFFDE7),
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1208).copy(alpha = 0.40f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = AccentAmber.copy(alpha = 0.20f),
                        spotColor = AccentAmber.copy(alpha = 0.20f),
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFDE7),
                                Color(0xFFFFF9C4),
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFD600).copy(alpha = 0.55f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable(enabled = false, onClick = {})
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFD600).copy(alpha = 0.80f),
                                    Color(0xFFFFD600).copy(alpha = 0.80f),
                                    Color.Transparent,
                                )
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "New Quote ✨",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 13.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = sunnyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author (optional)", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = sunnyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(
                                elevation = if (canConfirm) 4.dp else 0.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = AccentAmber.copy(alpha = 0.30f),
                                spotColor = AccentAmber.copy(alpha = 0.30f),
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (canConfirm)
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFD600),
                                            Color(0xFFFFB300),
                                        )
                                    )
                                else
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFE0E0E0),
                                            Color(0xFFE0E0E0),
                                        )
                                    )
                            )
                            .clickable(enabled = canConfirm, onClick = { onConfirm(text, author) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add Quote",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canConfirm) Color(0xFF1A1208) else Color(0xFF9E9E9E),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SunnyEditQuoteDialog(
    quote: Quote,
    onSave: (Quote) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(quote.text) }
    var author by remember { mutableStateOf(quote.author) }
    val canSave = text.isNotBlank()

    val sunnyFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AccentAmber,
        focusedBorderColor = AccentAmber,
        unfocusedBorderColor = Color(0xFFFFD600).copy(alpha = 0.60f),
        focusedLabelColor = AccentAmberDark,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = Color(0xFFFFFDE7),
        unfocusedContainerColor = Color(0xFFFFFDE7),
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1208).copy(alpha = 0.40f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = AccentAmber.copy(alpha = 0.20f),
                        spotColor = AccentAmber.copy(alpha = 0.20f),
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFDE7),
                                Color(0xFFFFF9C4),
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFD600).copy(alpha = 0.55f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable(enabled = false, onClick = {})
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFD600).copy(alpha = 0.80f),
                                    Color(0xFFFFD600).copy(alpha = 0.80f),
                                    Color.Transparent,
                                )
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Edit Quote",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 13.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = sunnyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author (optional)", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = sunnyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFEBEE))
                                .border(
                                    width = 1.dp,
                                    color = DeleteRed.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(onClick = onDelete),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = DeleteRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(50.dp)
                                .shadow(
                                    elevation = if (canSave) 4.dp else 0.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = AccentAmber.copy(alpha = 0.30f),
                                    spotColor = AccentAmber.copy(alpha = 0.30f),
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (canSave)
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFFFD600),
                                                Color(0xFFFFB300),
                                            )
                                        )
                                    else
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFE0E0E0),
                                                Color(0xFFE0E0E0),
                                            )
                                        )
                                )
                                .clickable(
                                    enabled = canSave,
                                    onClick = { onSave(quote.copy(text = text.trim(), author = author.trim())) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canSave) Color(0xFF1A1208) else Color(0xFF9E9E9E),
                            )
                        }
                    }
                }
            }
        }
    }
}
