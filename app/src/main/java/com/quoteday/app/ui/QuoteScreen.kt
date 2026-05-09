package com.quoteday.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.quoteday.app.R
import com.quoteday.app.data.Quote
import com.quoteday.app.ui.QuoteViewModel.Companion.FREE_QUOTE_LIMIT
import com.quoteday.app.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@Composable
fun QuoteScreen(viewModel: QuoteViewModel, onSettingsClick: () -> Unit) {
    val colors = LocalAppColors.current
    val quotes by viewModel.quotes.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val limitReached by viewModel.limitReached.collectAsState()
    val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
    val productPrice by viewModel.productPrice.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingQuote by remember { mutableStateOf<Quote?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = colors.textPrimary,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = colors.buttonBackground,
                        contentColor = colors.buttonContent,
                        shape = RoundedCornerShape(10.dp),
                    )
                }
            },
            topBar = {
                JapandiHeader(
                    onSettingsClick = onSettingsClick,
                    quoteCount = quotes.size,
                    isPremium = isPremium,
                )
            },
            floatingActionButton = {
                JapandiFab(
                    limitReached = limitReached,
                    onClick = {
                        if (limitReached) viewModel.triggerUpgradePrompt()
                        else showDialog = true
                    }
                )
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
                        start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quotes, key = { it.firestoreId }) { quote ->
                        JapandiQuoteItem(
                            quote = quote,
                            onClick = { editingQuote = quote },
                            onCopied = {
                                scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                            }
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

    if (showUpgradePrompt) {
        val activity = LocalContext.current as android.app.Activity
        JapandiUpgradeDialog(
            productPrice = productPrice,
            onUpgradeClick = {
                viewModel.dismissUpgradePrompt()
                viewModel.launchPurchase(activity)
            },
            onDismiss = { viewModel.dismissUpgradePrompt() }
        )
    }
}

@Composable
private fun JapandiHeader(onSettingsClick: () -> Unit, quoteCount: Int, isPremium: Boolean) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 8.dp, top = 18.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = "QuoteDay logo",
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "QuoteDay",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "your daily words",
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    letterSpacing = 1.5.sp,
                )
                if (!isPremium) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$quoteCount / $FREE_QUOTE_LIMIT quotes",
                        fontSize = 10.sp,
                        color = if (quoteCount >= FREE_QUOTE_LIMIT)
                            colors.deleteRed.copy(alpha = 0.8f)
                        else
                            colors.accentMustard.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.accentWarm,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.cardBorder.copy(alpha = 0.5f))
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun JapandiQuoteItem(quote: Quote, onClick: () -> Unit, onCopied: () -> Unit) {
    val colors = LocalAppColors.current
    val clipboard = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = colors.overlay.copy(alpha = 0.06f),
                spotColor = colors.overlay.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = colors.accentMustard.copy(alpha = 0.12f)),
                onClick = onClick,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 18.dp, bottom = 18.dp, end = 4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_quote),
                contentDescription = null,
                tint = colors.accentMustard,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote.text,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = colors.textPrimary,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp,
                    maxLines = 4,
                    letterSpacing = 0.2.sp,
                )
                if (quote.author.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— ${quote.author}",
                        fontSize = 11.sp,
                        color = colors.accentWarm,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 0.5.sp,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .clickable {
                        val copyText = if (quote.author.isNotBlank())
                            "${quote.text} — ${quote.author}"
                        else
                            quote.text
                        clipboard.setText(AnnotatedString(copyText))
                        onCopied()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = colors.textMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun JapandiEmptyState(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "“",
                fontSize = 64.sp,
                color = colors.accentMustard.copy(alpha = 0.35f),
                fontWeight = FontWeight.Light,
                lineHeight = 56.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No quotes yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                letterSpacing = 0.8.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "tap  +  to begin",
                fontSize = 13.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
            )
        }
    }
}

@Composable
private fun JapandiFab(limitReached: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .padding(bottom = 28.dp, end = 8.dp)
            .size(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = colors.overlay.copy(alpha = 0.25f),
                spotColor = colors.overlay.copy(alpha = 0.25f),
            )
            .clip(CircleShape)
            .background(colors.buttonBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (limitReached) Icons.Default.Lock else Icons.Default.Add,
            contentDescription = if (limitReached) "Upgrade to add more" else "Add quote",
            tint = colors.buttonContent,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun JapandiAddQuoteDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val canConfirm = text.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        cursorColor = colors.accentMustard,
        focusedBorderColor = colors.accentMustard,
        unfocusedBorderColor = colors.cardBorder,
        focusedLabelColor = colors.accentMustard,
        unfocusedLabelColor = colors.textMuted,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.overlay.copy(alpha = 0.35f))
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
                        ambientColor = colors.overlay.copy(alpha = 0.12f),
                        spotColor = colors.overlay.copy(alpha = 0.12f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
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
                        color = colors.textPrimary,
                        letterSpacing = 0.8.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(28.dp).height(1.dp).background(colors.accentMustard))
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp),
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (canConfirm) colors.buttonBackground else colors.cardBorder)
                            .clickable(enabled = canConfirm, onClick = { onConfirm(text, author) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (canConfirm) colors.buttonContent else colors.textMuted,
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
    val colors = LocalAppColors.current
    var text by remember { mutableStateOf(quote.text) }
    var author by remember { mutableStateOf(quote.author) }
    val canSave = text.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        cursorColor = colors.accentMustard,
        focusedBorderColor = colors.accentMustard,
        unfocusedBorderColor = colors.cardBorder,
        focusedLabelColor = colors.accentMustard,
        unfocusedLabelColor = colors.textMuted,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.overlay.copy(alpha = 0.35f))
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
                        ambientColor = colors.overlay.copy(alpha = 0.12f),
                        spotColor = colors.overlay.copy(alpha = 0.12f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
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
                        color = colors.textPrimary,
                        letterSpacing = 0.8.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(28.dp).height(1.dp).background(colors.accentMustard))
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Quote", fontSize = 12.sp, letterSpacing = 0.5.sp) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
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
                                .border(1.dp, colors.deleteRed.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                                .clickable(onClick = onDelete),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = colors.deleteRed.copy(alpha = 0.75f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (canSave) colors.buttonBackground else colors.cardBorder)
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
                                color = if (canSave) colors.buttonContent else colors.textMuted,
                                letterSpacing = 1.5.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JapandiUpgradeDialog(productPrice: String?, onUpgradeClick: () -> Unit, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.overlay.copy(alpha = 0.35f))
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
                        ambientColor = colors.overlay.copy(alpha = 0.12f),
                        spotColor = colors.overlay.copy(alpha = 0.12f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .clickable(enabled = false, onClick = {})
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“",
                        fontSize = 52.sp,
                        color = colors.accentMustard.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Light,
                        lineHeight = 44.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unlock Unlimited",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                        letterSpacing = 0.8.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(28.dp).height(1.dp).background(colors.accentMustard))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You've reached the $FREE_QUOTE_LIMIT-quote limit.\nUpgrade once to add as many quotes as you like.",
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        letterSpacing = 0.3.sp,
                    )
                    if (productPrice != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "One-time purchase · $productPrice",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.accentMustard,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.buttonBackground)
                            .clickable(onClick = onUpgradeClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Upgrade",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.buttonContent,
                            letterSpacing = 1.5.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not now",
                            fontSize = 13.sp,
                            color = colors.textMuted,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }
        }
    }
}
