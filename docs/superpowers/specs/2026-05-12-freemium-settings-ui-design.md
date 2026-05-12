# Freemium Settings UI — Design Spec
_Date: 2026-05-12_

## Overview

Surface the user's current plan (Lite or Premium) in the Settings screen. Free users see a Lite badge and an upgrade card; premium users see a Premium badge and a status card. The existing billing and purchase flow infrastructure is already complete — this spec covers only the UI additions.

## Scope

- Small plan badge in the Settings TopAppBar
- Plan card in the PURCHASE section
- Three new parameters wired into `SettingsScreen`

**Out of scope:** Additional premium features (planned for a future version). The only premium perk is unlimited quotes.

---

## Section 1 — Parameters & Wiring

`SettingsScreen` gains three new parameters:

```kotlin
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onRestorePurchases: () -> Unit,
    isPremium: Boolean,
    productPrice: String?,
    onUpgradeClick: () -> Unit,
)
```

At the call site, these are sourced from `QuoteViewModel`:

- `isPremium` ← `quoteViewModel.isPremium.collectAsState()`
- `productPrice` ← `quoteViewModel.productPrice.collectAsState()`
- `onUpgradeClick` ← `{ quoteViewModel.triggerUpgradePrompt() }`

`triggerUpgradePrompt()` sets `showUpgradePrompt = true` in `QuoteViewModel`, which surfaces the existing `UpgradeDialog` already rendered in `QuoteScreen`. No new dialog is needed.

---

## Section 2 — Plan Badge in TopAppBar

A second `Text` line is added inside the `Column` in the `TopAppBar` title slot, directly below "Settings":

| State   | Text        | Color              | Style      |
|---------|-------------|--------------------|------------|
| Lite    | `LITE`      | `colors.textMuted` | `labelSmall`, `letterSpacing = 1.sp` |
| Premium | `★ PREMIUM` | `colors.accentMustard` | `labelSmall`, `letterSpacing = 1.sp` |

This mirrors the existing pattern in `QuoteScreen`'s TopAppBar which shows the quote count subtitle for free users.

---

## Section 3 — Plan Card in PURCHASE Section

Inserted **above** the existing "Restore Purchase" card in the `PURCHASE` section.

### Lite user (tappable, calls `onUpgradeClick`)

- **Headline:** `"Current Plan"`, `FontWeight.Medium`
- **Supporting:** `"Lite · Up to $FREE_QUOTE_LIMIT quotes"` — plus `" · $productPrice"` appended if price is loaded
- **Trailing:** Small `OutlinedButton` labeled `"Upgrade"` (same style/size as the "Test" button in the notification card), calls `onUpgradeClick`

### Premium user (non-tappable, status only)

- **Headline:** `"Current Plan"`, `FontWeight.Medium`
- **Supporting:** `"Premium · Unlimited quotes"`, color `colors.accentMustard`
- **Trailing:** `Text("★", color = colors.accentMustard)`

---

## Files Changed

| File | Change |
|------|--------|
| `ui/SettingsScreen.kt` | Add 3 parameters; badge in TopAppBar; plan card in PURCHASE section |
| `MainActivity.kt` (or wherever `SettingsScreen` is called) | Pass `isPremium`, `productPrice`, `onUpgradeClick` from `QuoteViewModel` |

---

## Design Decisions

- **Approach A chosen** (pass state as parameters) over giving `SettingsViewModel` its own Firestore observer — avoids duplicate observation and matches existing `onRestorePurchases` callback pattern.
- **`UpgradeDialog` reused** — tapping Upgrade in settings triggers `triggerUpgradePrompt()`, surfacing the existing dialog. No new paywall screen needed.
- **`FREE_QUOTE_LIMIT` constant reused** in supporting text to stay in sync automatically.
