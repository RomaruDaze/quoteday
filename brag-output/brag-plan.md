# Brag Plan: QuoteDay

## What is this app?
An Android app where you save your favorite quotes and it sends you one of them each day, picked at random, as a notification and on a Today card. Mustard and cream "Japandi" look, Google sign-in, free tier capped at 20 quotes.

## The angle
We pitch it like a Series A launch from 2016, dead serious. The deck calls "shows you a quote you already saved" a category-defining market. The funny part is real: the "proprietary algorithm" in `QuoteViewModel.kt` is literally `list.random()`. We show that code on a 2016-style metrics slide. The app is from 2026, so the outro calls it "ten years in stealth."

## Hook (first 2-3 seconds)
A 2016 deck title slide: "2016." Then a checklist of app ideas lands on the beat: Laundry ✓ Parking ✓ Dogs ✓. The founder voice says it as if it matters.

## Key moments
- A "Words" row with an empty checkbox, then a "SERIES A · CLOSED" stamp over the QuoteDay logo.
- Using the app: the New Quote dialog types "All is well" / "Three Idiots", Add gets tapped, and the card drops into the list.
- A "Quote of the Day" notification slides in, then the Today card shows the quote in serif italic.
- The metrics slide shows `Algorithm: list.random()`.

## Outro / punchline
QuoteDay logo, "your daily words", "Ten years in stealth." and "Now on Google Play."

## User flow worth showing
Add a quote (New Quote dialog), get the daily notification, see the Today card.

## Tone
- Preset: yc-parody
- Creative direction: fake Series A launch from 2016
- Interpretation: flat 2016 deck look (Helvetica Neue, thin rules, checkmarks, a big metric table), hard cuts, a calm founder voice, and no winking. The joke is the real code and the real app treated like a unicorn.

## Format: landscape — 1920x1080
## Duration: 25s (the voice sets the pace)

## Visual identity (from the project)
- Background: gradient #FFFFFF → #FFF3A0 → #FFDE59 (app light), deck slides on #1C1910
- Surface: #FFFCF0, card border #E8C830
- Accent: #B5892A (mustard), #CA9B38 on dark
- Text: #1C1910 / #FCF8EC on dark; secondary #6B5E38
- Display font: Helvetica Neue (the 2016 deck look). Quote text uses serif italic (the app uses FontFamily.Serif Italic).
- Strongest visual element: the rounded Today quote card with the ❞ glyph, and the quill logo

## Share copy (draft)
In 2016 every problem got an app. We found the last one: words. QuoteDay sends you one of your own quotes every day, powered by a proprietary algorithm (list.random()).

## Audio direction
- Role: sparse corporate bed under a founder voiceover
- Music: happy-beats-business-moves-vol-11 (warm, business-y, 114.8 BPM)
- Music treatment: 0.14 under the voice, fade out over the last 0.8s
- Music cue guidance: preset `assets/music/cues/...vol-11...music-cues.json`. Strong cues 8.96s (Series A stamp) and 22.65s (Google Play line). Hook checklist rows go on every other beat after 1.60s (≥0.8s holds).
- Audio-reactive treatment: subtle warmth on the outro logo glow only
- SFX posture: sparse. Stamp thud, typed keys, Add click, card drop, notification plate, final bell.
- Restraint rule: nothing loud over the voice, and no whooshes.

## Voiceover script (Kokoro, am_michael, 0.95x)
1. "In twenty sixteen, everything got an app. Laundry. Parking. Dogs." (4.63s)
2. "But one market sat untouched. Words. So we raised a Series A." (4.67s)
3. "You save the quotes that matter." (1.96s)
4. "Every morning, we deliver one." (2.05s)
5. "Powered by a proprietary algorithm." (2.45s)
6. "QuoteDay. Your daily words. Ten years in stealth. Now on Google Play." (4.91s)

## Storyboard

### Scene 1 — Hook — 0–4.9s
A dark deck slide with "2016." in the corner, the headline "Everything got an app.", and checklist rows Laundry ✓ / Parking ✓ / Dogs ✓ arriving about 0.8s apart.
Sequential: yes. Audio: quiet key ticks. Transition: hard cut.

### Scene 2 — The gap → Series A — 4.9–9.8s
Same slide, and the checklist dims. A 4th row "Words" with an empty box enlarges. Cut to cream: the quill logo, "QuoteDay", and a stamp "SERIES A · CLOSED" (beat-locked 8.96).
Audio: soft impact on the stamp. Transition: hard cut.

### Scene 3 — Save — 9.8–13.4s
A phone on the mustard gradient shows the New Quote dialog. "All is well" types in, the author "Three Idiots" appears, Add gets tapped, and the card lands in the list.
Interaction: typing + tap. Audio: keys, click, drop.

### Scene 4 — Deliver — 13.4–16.6s
The phone shows the Today screen. A "Quote of the Day" notification slides down, then the card crossfades to "In the middle of every difficulty lies opportunity. — Albert Einstein" with the date "Thursday, September 17".
Audio: light plate ping.

### Scene 5 — The algorithm — 16.6–19.5s
A dark metrics slide with rows: "Quotes per day: 1" / "Free tier: 20 quotes" / "Algorithm: list.random()" (the last row in mono, highlighted in mustard). Hold 1.2s.

### Scene 6 — Outro — 19.5–25.0s
Cream gradient with the logo, "QuoteDay", "your daily words", then "Ten years in stealth." and "Now on Google Play." (22.65 cue). Bell.

**Music mood:** parody corporate
**Audio summary:** A calm founder voice over a low business bed, dry UI sounds for the real app interactions, and one bell at the end.
