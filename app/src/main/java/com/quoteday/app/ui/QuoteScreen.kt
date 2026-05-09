package com.quoteday.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

private enum class Tab { Today, Quotes }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(viewModel: QuoteViewModel, onSettingsClick: () -> Unit) {
    val colors = LocalAppColors.current
    val quotes by viewModel.quotes.collectAsState()
    val todayQuote by viewModel.todayQuote.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val limitReached by viewModel.limitReached.collectAsState()
    val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
    val productPrice by viewModel.productPrice.collectAsState()
    var selectedTab by remember { mutableStateOf(Tab.Today) }
    var showDialog by remember { mutableStateOf(false) }
    var editingQuote by remember { mutableStateOf<Quote?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.background)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "QuoteDay",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary,
                                letterSpacing = 0.5.sp,
                            )
                            if (selectedTab == Tab.Quotes && !isPremium) {
                                Text(
                                    text = "${quotes.size} / $FREE_QUOTE_LIMIT quotes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (quotes.size >= FREE_QUOTE_LIMIT)
                                        colors.deleteRed.copy(alpha = 0.8f)
                                    else
                                        colors.accentMustard,
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_logo),
                            contentDescription = "QuoteDay logo",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(40.dp),
                        )
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = colors.accentWarm,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surface,
                        scrolledContainerColor = colors.surface,
                    ),
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = colors.surface,
                    tonalElevation = 0.dp,
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.accentMustard,
                        selectedTextColor = colors.accentMustard,
                        indicatorColor = colors.accentMustard.copy(alpha = 0.15f),
                        unselectedIconColor = colors.textMuted,
                        unselectedTextColor = colors.textMuted,
                    )
                    NavigationBarItem(
                        selected = selectedTab == Tab.Today,
                        onClick = { selectedTab = Tab.Today },
                        icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = "Today") },
                        label = { Text("Today") },
                        colors = navItemColors,
                    )
                    NavigationBarItem(
                        selected = selectedTab == Tab.Quotes,
                        onClick = { selectedTab = Tab.Quotes },
                        icon = { Icon(Icons.Outlined.FormatQuote, contentDescription = "Quotes") },
                        label = { Text("Quotes") },
                        colors = navItemColors,
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == Tab.Quotes) {
                    FloatingActionButton(
                        onClick = {
                            if (limitReached) viewModel.triggerUpgradePrompt()
                            else showDialog = true
                        },
                        containerColor = colors.buttonBackground,
                        contentColor = colors.buttonContent,
                    ) {
                        Icon(
                            imageVector = if (limitReached) Icons.Default.Lock else Icons.Default.Add,
                            contentDescription = if (limitReached) "Upgrade to add more" else "Add quote",
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                Tab.Today -> TodayQuoteScreen(
                    quote = todayQuote,
                    modifier = Modifier.padding(padding),
                )
                Tab.Quotes -> {
                    if (quotes.isEmpty()) {
                        QuoteEmptyState(
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
                                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quotes, key = { it.firestoreId }) { quote ->
                                QuoteCard(
                                    quote = quote,
                                    onClick = { editingQuote = quote },
                                    onCopied = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Copied to clipboard")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddQuoteDialog(
            onConfirm = { text, author ->
                viewModel.addQuote(text, author)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    editingQuote?.let { quote ->
        EditQuoteDialog(
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
        UpgradeDialog(
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
private fun QuoteEmptyState(modifier: Modifier = Modifier) {
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No quotes yet",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "tap  +  to begin",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
            )
        }
    }
}

@Composable
private fun QuoteCard(quote: Quote, onClick: () -> Unit, onCopied: () -> Unit) {
    val colors = LocalAppColors.current
    val clipboard = LocalClipboardManager.current

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.cardBorder),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_quote),
                contentDescription = null,
                tint = colors.accentMustard,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = colors.textPrimary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4,
                )
                if (quote.author.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.accentWarm,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(
                onClick = {
                    val copyText = if (quote.author.isNotBlank())
                        "${quote.text} — ${quote.author}"
                    else
                        quote.text
                    clipboard.setText(AnnotatedString(copyText))
                    onCopied()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = colors.textMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun AddQuoteDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val canConfirm = text.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.accentMustard,
        focusedLabelColor = colors.accentMustard,
        cursorColor = colors.accentMustard,
        unfocusedBorderColor = colors.cardBorder,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.cardBorder),
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "New Quote",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 20.dp)
                        .width(28.dp),
                    color = colors.accentMustard,
                    thickness = 2.dp,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Quote") },
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
                    label = { Text("Author (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = fieldColors,
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(text, author) },
                        enabled = canConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.buttonBackground,
                            contentColor = colors.buttonContent,
                        ),
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditQuoteDialog(
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
        focusedBorderColor = colors.accentMustard,
        focusedLabelColor = colors.accentMustard,
        cursorColor = colors.accentMustard,
        unfocusedBorderColor = colors.cardBorder,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.cardBorder),
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Edit Quote",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 20.dp)
                        .width(28.dp),
                    color = colors.accentMustard,
                    thickness = 2.dp,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Quote") },
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
                    label = { Text("Author (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = fieldColors,
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        border = BorderStroke(1.dp, colors.deleteRed.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.deleteRed),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onSave(quote.copy(text = text.trim(), author = author.trim())) },
                            enabled = canSave,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.buttonBackground,
                                contentColor = colors.buttonContent,
                            ),
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpgradeDialog(productPrice: String?, onUpgradeClick: () -> Unit, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(
                text = "“",
                fontSize = 48.sp,
                color = colors.accentMustard.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light,
                lineHeight = 40.sp,
            )
        },
        title = {
            Text(
                text = "Unlock Unlimited",
                textAlign = TextAlign.Center,
                color = colors.textPrimary,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You've reached the $FREE_QUOTE_LIMIT-quote limit. Upgrade once to add as many quotes as you like.",
                    textAlign = TextAlign.Center,
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (productPrice != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "One-time purchase · $productPrice",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.accentMustard,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonBackground,
                    contentColor = colors.buttonContent,
                ),
            ) {
                Text("Upgrade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not now") }
        },
        containerColor = colors.surface,
        iconContentColor = colors.accentMustard,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textSecondary,
    )
}
