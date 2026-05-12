package com.quoteday.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quoteday.app.BuildConfig
import com.quoteday.app.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit, onRestorePurchases: () -> Unit) {
    val colors = LocalAppColors.current
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationHour by viewModel.notificationHour.collectAsState()
    val notificationMinute by viewModel.notificationMinute.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationHour,
            initialMinute = notificationMinute,
            is24Hour = false,
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.setNotificationTime(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.buttonBackground,
                                contentColor = colors.buttonContent,
                            ),
                        ) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textPrimary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.textPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surface,
                        scrolledContainerColor = colors.surface,
                    ),
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SettingsSectionLabel("NOTIFICATIONS")

                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.cardBorder),
                ) {
                    ListItem(
                        headlineContent = {
                            Text("Daily Notification", fontWeight = FontWeight.Medium)
                        },
                        supportingContent = { Text("Receive a quote every day") },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.testNotification() },
                                    enabled = notificationEnabled,
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                    modifier = Modifier.height(32.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (notificationEnabled) colors.cardBorder
                                        else colors.cardBorder.copy(alpha = 0.4f),
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = colors.textSecondary,
                                        disabledContentColor = colors.textMuted,
                                    ),
                                ) {
                                    Text("Test", fontSize = 12.sp, letterSpacing = 0.5.sp)
                                }
                                Switch(
                                    checked = notificationEnabled,
                                    onCheckedChange = { viewModel.setNotificationEnabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = colors.accentMustard,
                                        checkedThumbColor = colors.surface,
                                    ),
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = colors.textPrimary,
                            supportingColor = colors.textSecondary,
                        ),
                    )
                }

                OutlinedCard(
                    onClick = { if (notificationEnabled) showTimePicker = true },
                    enabled = notificationEnabled,
                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
                    border = BorderStroke(
                        1.dp,
                        if (notificationEnabled) colors.cardBorder else colors.cardBorder.copy(alpha = 0.4f),
                    ),
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                "Notification Time",
                                fontWeight = FontWeight.Medium,
                                color = if (notificationEnabled) colors.textPrimary else colors.textMuted,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = formatNotifTime(notificationHour, notificationMinute),
                                color = if (notificationEnabled) colors.accentMustard else colors.textMuted,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = if (notificationEnabled) colors.textSecondary else colors.textMuted,
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionLabel("PURCHASE")

                OutlinedCard(
                    onClick = onRestorePurchases,
                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.cardBorder),
                ) {
                    ListItem(
                        headlineContent = {
                            Text("Restore Purchase", fontWeight = FontWeight.Medium)
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = colors.textSecondary,
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = colors.textPrimary,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = { viewModel.testNotification() },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.textSecondary,
                    ),
                ) {
                    Text("Send Test Notification", letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionLabel("ABOUT")

                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.cardBorder),
                ) {
                    ListItem(
                        headlineContent = {
                            Text("Version", fontWeight = FontWeight.Medium)
                        },
                        trailingContent = {
                            Text(
                                text = BuildConfig.VERSION_NAME,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = colors.textPrimary,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    val colors = LocalAppColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = colors.textMuted,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

private fun formatNotifTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = when {
        hour == 0  -> 12
        hour > 12  -> hour - 12
        else       -> hour
    }
    return "%d:%02d %s".format(h, minute, amPm)
}
