# Settings UI Improvements — Design Spec
_Date: 2026-05-12_

## Overview

Two small UI improvements to `SettingsScreen.kt`:
1. Move the "Send Test Notification" button inside the Daily Notification card, next to the toggle switch.
2. Add a "Contact Us" row in a new SUPPORT section that opens the email app.

---

## Feature 1: Test Notification Chip in Daily Notification Card

### What changes
The standalone `OutlinedButton` ("Send Test Notification") that currently sits below the PURCHASE section is **removed**. Instead, a compact "Test" chip is placed to the **right of the Switch** inside the Daily Notification card's `trailingContent`.

### Layout
`trailingContent` becomes a `Row(verticalAlignment = CenterVertically, horizontalArrangement = spacedBy(8.dp))` containing:
- A small outlined chip: `TextButton` with a `BorderStroke(1.dp, colors.cardBorder)` border and label `"Test"`, using `letterSpacing = 0.5.sp` to match existing button style.
- The existing `Switch` (unchanged).

### Behavior
- Chip calls `viewModel.testNotification()` on click.
- Chip is `enabled = notificationEnabled`. When disabled, it uses `colors.textMuted` for content color (dimmed appearance).

---

## Feature 2: Contact Us — SUPPORT Section

### What changes
A new section is inserted **above** the existing `ABOUT` section label.

### Layout
```
SettingsSectionLabel("SUPPORT")
OutlinedCard(onClick = { /* launch email intent */ }) {
    ListItem(
        headlineContent  = { Text("Contact Us") }
        supportingContent = { Text("Get help or send feedback") }
        trailingContent  = { Icon(ChevronRight) }
    )
}
```
Follows the identical visual pattern as the existing "Restore Purchase" card.

### Behavior
On tap, fires:
```kotlin
Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf("romarudazee99@gmail.com"))
    putExtra(Intent.EXTRA_SUBJECT, "QuoteDay Support")
}
```
Wrapped in `try/catch(ActivityNotFoundException)` — silently no-ops if no email app is installed.

### Imports needed (additions)
- `android.content.ActivityNotFoundException`
- `android.content.Intent`
- `android.net.Uri`
- `androidx.compose.ui.platform.LocalContext`

---

## Files Changed
| File | Change |
|------|--------|
| `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt` | All changes — no other files |

---

## Out of Scope
- Email address is hardcoded for now; no settings-driven value.
- No toast/snackbar shown if no email app is present.
- No changes to `SettingsViewModel`.
