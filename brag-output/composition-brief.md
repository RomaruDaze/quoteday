# Hyperframes Composition Brief: QuoteDay

## Objective
A 25s launch-style brag video for QuoteDay: a fake 2016 Series A launch played straight.

## Output
- Composition: `brag-output/composition/`
- Render: `brag-output/brag.mp4`
- Landscape 1920x1080, 25s

## Source Material
- Project root: /Users/rogermarvin/Projects/personal/quoteday
- Files read: ui/QuoteScreen.kt, ui/TodayQuoteScreen.kt, ui/SplashScreen.kt, ui/SignInScreen.kt, ui/QuoteViewModel.kt, ui/theme/AppColors.kt, ui/theme/Theme.kt, notification/NotificationHelper.kt, docs/assets/store-sc-1/2.jpeg
- Verbatim copy: "QuoteDay", "your daily words", "New Quote", "Quote", "Author (optional)", "Add", "Quote of the Day", "Today", "Quotes", "All is well — Three Idiots", "In the middle of every difficulty lies opportunity. — Albert Einstein", `list.random()`, "20" (FREE_QUOTE_LIMIT)
- Logo: `assets/img/ic_logo.png` (from res/drawable-nodpi)

## Creative Direction
- yc-parody / "fake Series A launch from 2016". See brag-plan.md for the angle, storyboard, and VO.
- Avoid generic SaaS language, filler visuals, and restyling the app.

## Visual Identity
Taken from AppColors.kt (light and dark). Deck slides use the dark palette (#1C1910 bg, #FCF8EC text, #CA9B38 accent). App scenes use the light gradient (#FFFFFF→#FFF3A0→#FFDE59), surface #FFFCF0, border #E8C830, accent #B5892A. Fonts: Helvetica Neue for the deck, Georgia italic for quotes, system mono for code. All local, no web fonts.

## Audio
- Music: vol-11 at 0.14, fade at the end. Cue preset in the brag skill assets. Beat-locks at 8.96 and 22.65.
- VO: `assets/vo/s1..s6.wav` at 0.1 / 5.0 / 9.9 / 13.5 / 16.7 / 19.6s
- SFX: impactSoft_medium_001 (stamp), keypress-003/007/009 (typing), click3 (Add), drop_001 (card lands), impactPlate_light_001 (notification), impactBell_heavy_000 (outro)
- Audio-reactive: subtle. Skipped if extraction is unavailable, with no hyperframes-creative skill installed locally.

## Hyperframes Instructions
Follow the local CLAUDE.md contract (paused root timeline, `data-start`/`data-duration` clips, deterministic JS) and pass `hyperframes check` before rendering.
