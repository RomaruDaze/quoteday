# Settings UI Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move the test notification button inside the Daily Notification card and add a Contact Us card in a new SUPPORT section.

**Architecture:** All changes are isolated to `SettingsScreen.kt`. The Daily Notification card's `trailingContent` becomes a `Row` holding a compact "Test" `OutlinedButton` and the existing `Switch`. The standalone full-width test button is removed. A new `SUPPORT` section with a `Contact Us` card is inserted above the existing `ABOUT` section.

**Tech Stack:** Kotlin, Jetpack Compose / Material3, Android Intent API

---

### Task 1: Replace standalone test button with chip inside Daily Notification card

**Files:**
- Modify: `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt`

- [ ] **Step 1: Change the Daily Notification card's `trailingContent` to a Row with chip + Switch**

In `SettingsScreen.kt`, find the `ListItem` inside the first `OutlinedCard` (the Daily Notification card, around line 123). Replace its `trailingContent` lambda with:

```kotlin
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
```

`Row`, `Arrangement`, `PaddingValues`, `height`, `BorderStroke`, `OutlinedButton`, `ButtonDefaults`, `Text` are all already imported via the existing wildcard imports — no new imports needed for this step.

- [ ] **Step 2: Remove the standalone OutlinedButton**

Delete these lines (the full-width "Send Test Notification" button block, currently sitting between the PURCHASE section and ABOUT section label):

```kotlin
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
```

- [ ] **Step 3: Verify it compiles**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt
git commit -m "feat: move test notification button into Daily Notification card"
```

---

### Task 2: Add Contact Us card in a new SUPPORT section

**Files:**
- Modify: `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt`

- [ ] **Step 1: Add required imports**

Add these four imports at the top of `SettingsScreen.kt` alongside the existing imports:

```kotlin
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
```

- [ ] **Step 2: Capture context inside the composable**

Near the top of `SettingsScreen`, after the existing `val colors = LocalAppColors.current` line, add:

```kotlin
val context = LocalContext.current
```

- [ ] **Step 3: Insert the SUPPORT section above ABOUT**

Find this line in the `Column` (currently around line 220):

```kotlin
Spacer(modifier = Modifier.height(8.dp))
SettingsSectionLabel("ABOUT")
```

Replace it with:

```kotlin
Spacer(modifier = Modifier.height(8.dp))
SettingsSectionLabel("SUPPORT")

OutlinedCard(
    onClick = {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("romarudazee99@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "QuoteDay Support")
            }
            context.startActivity(Intent.createChooser(intent, "Send email"))
        } catch (_: ActivityNotFoundException) { }
    },
    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
    border = BorderStroke(1.dp, colors.cardBorder),
) {
    ListItem(
        headlineContent = {
            Text("Contact Us", fontWeight = FontWeight.Medium)
        },
        supportingContent = { Text("Get help or send feedback") },
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
            supportingColor = colors.textSecondary,
        ),
    )
}

Spacer(modifier = Modifier.height(8.dp))
SettingsSectionLabel("ABOUT")
```

- [ ] **Step 4: Verify it compiles**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt
git commit -m "feat: add Contact Us card in new Support section"
```

---

### Task 3: Manual smoke test

- [ ] **Step 1: Build a debug APK and install**

```bash
./gradlew :app:installDebug
```

- [ ] **Step 2: Verify Daily Notification card**

Open the app → Settings.
- Confirm the "Test" chip appears to the left of the notification toggle inside the Daily Notification card.
- Toggle notifications OFF — confirm the "Test" chip becomes dimmed and non-tappable.
- Toggle notifications ON — tap "Test" chip — confirm a test notification appears.
- Confirm the old full-width "Send Test Notification" button is gone.

- [ ] **Step 3: Verify Contact Us**

Scroll to the SUPPORT section (above ABOUT).
- Confirm "Contact Us" card with "Get help or send feedback" subtitle is visible.
- Tap it — confirm the email chooser or email app opens with `romarudazee99@gmail.com` pre-filled and subject "QuoteDay Support".

- [ ] **Step 4: Final commit if any last tweaks were needed**

```bash
git add -p
git commit -m "fix: settings UI smoke test adjustments"
```
