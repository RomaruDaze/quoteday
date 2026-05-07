package com.quoteday.app.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import com.quoteday.app.R
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

private val Background    = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFFF3A0),
        Color(0xFFFFDE59),
    ),
    startY = 0f,
    endY = 2200f
)
private val Surface       = Color(0xFFFFFCF0)
private val CardBorder    = Color(0xFFE8C830)
private val AccentMustard = Color(0xFFB5892A)
private val AccentWarm    = Color(0xFF7A6A45)
private val TextPrimary   = Color(0xFF1C1910)
private val TextSecondary = Color(0xFF6B5E38)
private val TextMuted     = Color(0xFFA09068)
private val Charcoal      = Color(0xFF3A2E18)
private val DeleteRed     = Color(0xFFC0392B)

@Composable
fun QuoteScreen(viewModel: QuoteViewModel) {
    val quotes by viewModel.quotes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingQuote by remember { mutableStateOf<Quote?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            topBar = { JapandiHeader() },
            floatingActionButton = {
                JapandiFab(onClick = { showDialog = true })
            }
        ) { padding ->
            if (quotes.isEmpty()) {
                JapandiEmptyState(
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
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quotes, key = { it.id }) { quote ->
                        JapandiQuoteItem(
                            quote = quote,
                            onClick = { editingQuote = quote }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        JapandiAddQuoteDialog(
            onConfirm = { text, author ->
                viewModel.addQuote(text, author)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    editingQuote?.let { quote ->
        JapandiEditQuoteDialog(
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
private fun JapandiHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "QuoteDay",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "your daily words",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    letterSpacing = 1.5.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CardBorder)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun JapandiQuoteItem(quote: Quote, onClick: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Charcoal.copy(alpha = 0.06f),
                spotColor = Charcoal.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 18.dp, bottom = 18.dp, end = 4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_quote),
                contentDescription = null,
                tint = AccentMustard,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
                    .padding(start = 0.dp)
                    .offset(x = 16.dp)
            )

            Spacer(modifier = Modifier.width(28.dp))

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = quote.text,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextPrimary,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp,
                    maxLines = 2,
                    letterSpacing = 0.2.sp,
                )
                if (quote.author.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— ${quote.author}",
                        fontSize = 11.sp,
                        color = AccentWarm,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 0.5.sp,
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
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun JapandiEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "“",
                fontSize = 64.sp,
                color = AccentMustard.copy(alpha = 0.35f),
                fontWeight = FontWeight.Light,
                lineHeight = 56.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No quotes yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                letterSpacing = 0.8.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "tap  +  to begin",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
            )
        }
    }
}

@Composable
private fun JapandiFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 28.dp, end = 8.dp)
            .size(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Charcoal.copy(alpha = 0.25f),
                spotColor = Charcoal.copy(alpha = 0.25f),
            )
            .clip(CircleShape)
            .background(Charcoal)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add quote",
            tint = Surface,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun JapandiAddQuoteDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val canConfirm = text.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AccentMustard,
        focusedBorderColor = AccentMustard,
        unfocusedBorderColor = CardBorder,
        focusedLabelColor = AccentMustard,
        unfocusedLabelColor = TextMuted,
        focusedContainerColor = Surface,
        unfocusedContainerColor = Surface,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Charcoal.copy(alpha = 0.35f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Charcoal.copy(alpha = 0.12f),
                        spotColor = Charcoal.copy(alpha = 0.12f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(
                        width = 1.dp,
                        color = CardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(enabled = false, onClick = {})
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp)
                ) {
                    Text(
                        text = "New Quote",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        letterSpacing = 0.8.sp,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(1.dp)
                            .background(AccentMustard)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author (optional)", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (canConfirm) Charcoal else CardBorder)
                            .clickable(enabled = canConfirm, onClick = { onConfirm(text, author) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (canConfirm) Surface else TextMuted,
                            letterSpacing = 1.5.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JapandiEditQuoteDialog(
    quote: Quote,
    onSave: (Quote) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(quote.text) }
    var author by remember { mutableStateOf(quote.author) }
    val canSave = text.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AccentMustard,
        focusedBorderColor = AccentMustard,
        unfocusedBorderColor = CardBorder,
        focusedLabelColor = AccentMustard,
        unfocusedLabelColor = TextMuted,
        focusedContainerColor = Surface,
        unfocusedContainerColor = Surface,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Charcoal.copy(alpha = 0.35f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Charcoal.copy(alpha = 0.12f),
                        spotColor = Charcoal.copy(alpha = 0.12f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(
                        width = 1.dp,
                        color = CardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(enabled = false, onClick = {})
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp)
                ) {
                    Text(
                        text = "Edit Quote",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        letterSpacing = 0.8.sp,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(1.dp)
                            .background(AccentMustard)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author (optional)", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = 1.dp,
                                    color = DeleteRed.copy(alpha = 0.40f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(onClick = onDelete),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = DeleteRed.copy(alpha = 0.75f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (canSave) Charcoal else CardBorder)
                                .clickable(
                                    enabled = canSave,
                                    onClick = { onSave(quote.copy(text = text.trim(), author = author.trim())) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (canSave) Surface else TextMuted,
                                letterSpacing = 1.5.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}
