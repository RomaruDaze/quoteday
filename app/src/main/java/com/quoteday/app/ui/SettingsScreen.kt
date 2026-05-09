package com.quoteday.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val SettingsBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFF3A0), Color(0xFFFFDE59)),
    startY = 0f, endY = 4000f
)
private val SettingsSurface   = Color(0xFFFFFCF0)
private val SettingsCardBorder = Color(0xFFE8C830)
private val SettingsAccent    = Color(0xFFB5892A)
private val SettingsTextPrimary   = Color(0xFF1C1910)
private val SettingsTextSecondary = Color(0xFF6B5E38)
private val SettingsTextMuted     = Color(0xFFA09068)
private val SettingsCharcoal      = Color(0xFF3A2E18)

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationHour by viewModel.notificationHour.collectAsState()
    val notificationMinute by viewModel.notificationMinute.collectAsState()
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        DisposableEffect(Unit) {
            val dialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    viewModel.setNotificationTime(hour, minute)
                    showTimePicker = false
                },
                notificationHour,
                notificationMinute,
                false
            ).apply {
                setOnCancelListener { showTimePicker = false }
                show()
            }
            onDispose { dialog.dismiss() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsSurface)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(SettingsSurface)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SettingsTextPrimary
                    )
                }
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = SettingsTextPrimary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SettingsCardBorder)
                    .align(Alignment.BottomCenter)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "NOTIFICATIONS",
                fontSize = 10.sp,
                color = SettingsTextMuted,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(2.dp))

            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Notification",
                            fontSize = 14.sp,
                            color = SettingsTextPrimary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Receive a quote every day",
                            fontSize = 12.sp,
                            color = SettingsTextSecondary,
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { viewModel.setNotificationEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SettingsSurface,
                            checkedTrackColor = SettingsAccent,
                            uncheckedThumbColor = SettingsTextMuted,
                            uncheckedTrackColor = SettingsCardBorder,
                        )
                    )
                }
            }

            SettingsCard(onClick = if (notificationEnabled) ({ showTimePicker = true }) else null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notification Time",
                            fontSize = 14.sp,
                            color = if (notificationEnabled) SettingsTextPrimary else SettingsTextMuted,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatNotifTime(notificationHour, notificationMinute),
                            fontSize = 12.sp,
                            color = if (notificationEnabled) SettingsAccent else SettingsTextMuted,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (notificationEnabled) SettingsTextSecondary else SettingsTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SettingsCharcoal)
                    .clickable { viewModel.testNotification() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Send Test Notification",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SettingsSurface,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SettingsSurface)
            .border(1.dp, SettingsCardBorder, RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        content()
    }
}

private fun formatNotifTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "%d:%02d %s".format(h, minute, amPm)
}
