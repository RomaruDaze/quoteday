# Freemium Settings UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Surface the user's Lite/Premium plan status in Settings via a TopAppBar badge and a plan card, with an upgrade path for free users.

**Architecture:** Three new parameters (`isPremium`, `productPrice`, `onUpgradeClick`) are added to `SettingsScreen`, sourced from `QuoteViewModel` at the `MainActivity` call site. `UpgradeDialog` is lifted from `private` in `QuoteScreen` to `internal`, then rendered at `MainActivity` level so it works regardless of which screen is active.

**Tech Stack:** Kotlin, Jetpack Compose, Material3, Google Play Billing (already wired)

---

## File Map

| File | Change |
|------|--------|
| `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt` | Add 3 params; badge in TopAppBar; plan card in PURCHASE section; import `FREE_QUOTE_LIMIT` |
| `app/src/main/java/com/quoteday/app/ui/QuoteScreen.kt` | Change `UpgradeDialog` from `private` to `internal`; remove `UpgradeDialog` rendering block from `QuoteScreen` |
| `app/src/main/java/com/quoteday/app/MainActivity.kt` | Collect `isPremium`, `productPrice`, `showUpgradePrompt`; render `UpgradeDialog` at top level; pass new params to `SettingsScreen` |

---

## Task 1: Add parameters and TopAppBar badge to SettingsScreen

**Files:**
- Modify: `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt`

- [ ] **Step 1: Add the three new parameters to `SettingsScreen`**

Replace the function signature (line 30):
```kotlin
// Before
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit, onRestorePurchases: () -> Unit) {

// After
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onRestorePurchases: () -> Unit,
    isPremium: Boolean,
    productPrice: String?,
    onUpgradeClick: () -> Unit,
) {
```

- [ ] **Step 2: Add the plan badge to the TopAppBar title slot**

Replace the `title` lambda of the `TopAppBar` (currently lines 91–98):
```kotlin
// Before
title = {
    Text(
        text = "Settings",
        style = MaterialTheme.typography.titleLarge,
        color = colors.textPrimary,
    )
},

// After
title = {
    Column {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
        )
        Text(
            text = if (isPremium) "★ PREMIUM" else "LITE",
            style = MaterialTheme.typography.labelSmall,
            color = if (isPremium) colors.accentMustard else colors.textMuted,
            letterSpacing = 1.sp,
        )
    }
},
```

- [ ] **Step 3: Add the `FREE_QUOTE_LIMIT` import**

Add to the import block at the top of `SettingsScreen.kt`:
```kotlin
import com.quoteday.app.ui.QuoteViewModel.Companion.FREE_QUOTE_LIMIT
```

- [ ] **Step 4: Build the project to verify it compiles**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL` (compilation only — the call site in `MainActivity` will fail until Task 3, but Kotlin compilation of `SettingsScreen.kt` alone should pass)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt
git commit -m "feat: add isPremium params and plan badge to SettingsScreen TopAppBar"
```

---

## Task 2: Add plan card to PURCHASE section

**Files:**
- Modify: `app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt`

- [ ] **Step 1: Add the plan card above "Restore Purchase" in the PURCHASE section**

Locate the `SettingsSectionLabel("PURCHASE")` line (currently around line 209). Insert a plan card between the label and the existing "Restore Purchase" card.

Replace this block:
```kotlin
Spacer(modifier = Modifier.height(8.dp))
SettingsSectionLabel("PURCHASE")

OutlinedCard(
    onClick = onRestorePurchases,
```

With:
```kotlin
Spacer(modifier = Modifier.height(8.dp))
SettingsSectionLabel("PURCHASE")

if (isPremium) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.cardBorder),
    ) {
        ListItem(
            headlineContent = {
                Text("Current Plan", fontWeight = FontWeight.Medium)
            },
            supportingContent = {
                Text(
                    text = "Premium · Unlimited quotes",
                    color = colors.accentMustard,
                )
            },
            trailingContent = {
                Text(
                    text = "★",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.accentMustard,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = colors.textPrimary,
            ),
        )
    }
} else {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.cardBorder),
    ) {
        ListItem(
            headlineContent = {
                Text("Current Plan", fontWeight = FontWeight.Medium)
            },
            supportingContent = {
                val priceText = if (productPrice != null) " · $productPrice" else ""
                Text("Lite · Up to $FREE_QUOTE_LIMIT quotes$priceText")
            },
            trailingContent = {
                OutlinedButton(
                    onClick = onUpgradeClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    border = BorderStroke(
                        1.dp,
                        colors.cardBorder,
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.textSecondary,
                    ),
                ) {
                    Text("Upgrade", fontSize = 12.sp, letterSpacing = 0.5.sp)
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = colors.textPrimary,
                supportingColor = colors.textSecondary,
            ),
        )
    }
}

OutlinedCard(
    onClick = onRestorePurchases,
```

Note: no extra `Spacer` is needed between the plan card and "Restore Purchase" — the parent `Column` uses `Arrangement.spacedBy(8.dp)` which provides the gap automatically.

- [ ] **Step 2: Build to verify it compiles**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/quoteday/app/ui/SettingsScreen.kt
git commit -m "feat: add Lite/Premium plan card to Settings PURCHASE section"
```

---

## Task 3: Lift UpgradeDialog and wire MainActivity

**Files:**
- Modify: `app/src/main/java/com/quoteday/app/ui/QuoteScreen.kt`
- Modify: `app/src/main/java/com/quoteday/app/MainActivity.kt`

### Part A — Expose UpgradeDialog from QuoteScreen

- [ ] **Step 1: Change `UpgradeDialog` visibility from `private` to `internal`**

In `QuoteScreen.kt` (around line 550), change:
```kotlin
// Before
@Composable
private fun UpgradeDialog(

// After
@Composable
internal fun UpgradeDialog(
```

- [ ] **Step 2: Remove the `UpgradeDialog` rendering block from `QuoteScreen`**

In `QuoteScreen`, remove the entire `if (showUpgradePrompt)` block (currently around lines 239–249):
```kotlin
// Remove this entire block:
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
```

Also remove the now-unused `productPrice` state collection in `QuoteScreen` (around line 57):
```kotlin
// Remove:
val productPrice by viewModel.productPrice.collectAsState()
```

- [ ] **Step 3: Build to verify it compiles**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

### Part B — Wire MainActivity

- [ ] **Step 4: Add state collection and `UpgradeDialog` rendering in `MainActivity.setContent`**

In `MainActivity.kt`, inside the `setContent { QuoteDayTheme { ... } }` block, add three new state collections alongside the existing `currentUser` collection, then render `UpgradeDialog` at the top level outside the `when` block.

Replace this block in `setContent`:
```kotlin
val currentUser by viewModel.currentUser.collectAsState()
var splashVisible by remember { mutableStateOf(true) }
var currentScreen by remember { mutableStateOf(Screen.Quote) }

if (splashVisible) {
    SplashScreen { splashVisible = false }
} else if (currentUser == null) {
    SignInScreen(onSignInClick = { launchGoogleSignIn() })
} else {
    when (currentScreen) {
        Screen.Quote -> QuoteScreen(
            viewModel = viewModel,
            onSettingsClick = { currentScreen = Screen.Settings }
        )
        Screen.Settings -> SettingsScreen(
            viewModel = settingsViewModel,
            onBack = { currentScreen = Screen.Quote },
            onRestorePurchases = { viewModel.restorePurchases() }
        )
    }
}
```

With:
```kotlin
val currentUser by viewModel.currentUser.collectAsState()
val isPremium by viewModel.isPremium.collectAsState()
val productPrice by viewModel.productPrice.collectAsState()
val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
var splashVisible by remember { mutableStateOf(true) }
var currentScreen by remember { mutableStateOf(Screen.Quote) }

if (splashVisible) {
    SplashScreen { splashVisible = false }
} else if (currentUser == null) {
    SignInScreen(onSignInClick = { launchGoogleSignIn() })
} else {
    when (currentScreen) {
        Screen.Quote -> QuoteScreen(
            viewModel = viewModel,
            onSettingsClick = { currentScreen = Screen.Settings }
        )
        Screen.Settings -> SettingsScreen(
            viewModel = settingsViewModel,
            onBack = { currentScreen = Screen.Quote },
            onRestorePurchases = { viewModel.restorePurchases() },
            isPremium = isPremium,
            productPrice = productPrice,
            onUpgradeClick = { viewModel.triggerUpgradePrompt() },
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
```

- [ ] **Step 5: Add the missing imports to `MainActivity.kt`**

Add to the import block:
```kotlin
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.quoteday.app.ui.UpgradeDialog
```

(Some of these may already be present — only add what's missing.)

- [ ] **Step 6: Full build to verify**

```bash
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/quoteday/app/ui/QuoteScreen.kt \
        app/src/main/java/com/quoteday/app/MainActivity.kt
git commit -m "feat: lift UpgradeDialog to MainActivity and wire freemium settings UI"
```

---

## Manual Smoke Test

After the build succeeds, install on a device or emulator and verify:

1. **Lite badge** — Open Settings → TopAppBar subtitle shows `LITE` in muted color
2. **Plan card (Lite)** — PURCHASE section shows "Current Plan / Lite · Up to 20 quotes" with an "Upgrade" button
3. **Upgrade flow** — Tap "Upgrade" in the plan card → `UpgradeDialog` appears
4. **Quote limit enforcement** — Confirm existing limit still works (add 20 quotes → FAB shows lock icon → tap → `UpgradeDialog`)
5. **Premium badge** (requires a test purchase or manually setting `isPremium: true` in Firestore) — TopAppBar shows `★ PREMIUM` in mustard; plan card shows "Premium · Unlimited quotes" with ★
6. **Restore Purchase** — Tapping "Restore Purchase" still works as before
