# Compose Components

Extra Jetpack Compose components that complement Material 3. Every component takes its colors, shapes and typography from `MaterialTheme`, so it fits whatever theme your app already uses.

## Installation

```kotlin
dependencies {
    implementation("io.github.nameisjayant:compose-components:<version>")
}
```

The library brings in Material 3 through the Compose BOM as an `api` dependency.

## Components

### ComposeDatePicker

A month calendar for picking **one date**, a **date range**, or **several dates**. Swipe or use the arrows to change month; tap the month title to jump to a year.

```kotlin
val state = rememberComposeDatePickerState(
    selectionMode = DateSelectionMode.Range,              // Single | Range | Multiple
    selectableDates = SelectableDates.notBefore(CalendarDate.today()) and SelectableDates.WeekdaysOnly,
)

ComposeDatePicker(
    state = state,
    colors = ComposeDatePickerDefaults.accentColors(Color(0xFF0F8A66)), // or .colors(...) / MaterialTheme
    dayShape = ComposeDatePickerDefaults.RoundedDayShape,
    decoration = { date -> if (date in eventDays) DayDecoration.dot() else null },
)

state.selectedDate      // Single
state.selectedRange     // Range  (start..end, null until both are picked)
state.selectedDates     // Multiple
```

| Feature | How |
| --- | --- |
| Selection modes | `DateSelectionMode.Single`, `Range`, `Multiple` (`maxSelectionCount` caps Multiple) |
| Disabled dates | `minDate` / `maxDate` + `SelectableDates` (`notBefore`, `notAfter`, `except`, `WeekdaysOnly`, `exceptDaysOfWeek`, combine with `and` / `or`) |
| Ranges over disabled days | `allowDisabledDatesInRange` (off by default: a new range starts instead) |
| Decorations | `DayDecoration(dots, label, labelColor, containerColor, contentColor)`, or replace the inside of each day with `dayContent` |
| Theming | `ComposeDatePickerDefaults.colors(...)` (from `MaterialTheme`), `accentColors(accent)`, `colors.copy(...)`, `shape`, `dayShape` |
| Layout options | `firstDayOfWeek`, `showAdjacentMonthDays`, `highlightToday`, `highlightWeekends`, `strikeThroughDisabledDays`, `yearSelectionEnabled`, custom/hidden `title` and `headline` |
| Dialog | `ComposeDatePickerDialog(onDismissRequest, confirmButton, dismissButton) { ComposeDatePicker(state) }` |

Dates use the library's own `CalendarDate` / `CalendarMonth` (no `java.time`, so no desugaring needed on minSdk 24). Convert with `toEpochMillisUtc()` / `CalendarDate.ofEpochMillisUtc(...)` and format with `date.format("yMMMd")`. The state survives configuration changes.

### ComposeRatingBar

A row of stars (or hearts, circles, any `Shape` or `Painter`) for showing and picking a rating. **Tap** an item to rate, or **drag** across the bar and the rating follows your finger.

```kotlin
var rating by rememberSaveable { mutableFloatStateOf(3f) }

ComposeRatingBar(
    rating = rating,
    onRatingChange = { rating = it },          // null = read-only
    stepSize = 0.5f,                           // 1 = whole, 0.5 = halves, 0 = free
    colors = ComposeRatingBarDefaults.accentColors(ComposeRatingBarDefaults.Gold),
)
```

| Feature | How |
| --- | --- |
| Input | Tap, horizontal drag, TalkBack adjust (`setProgress`); `allowClear` (tap the current rating to reset), `minRating`, `enabled`, `onRatingChangeFinished` |
| Steps | `stepSize` — `1f`, `0.5f`, `0f` (any value); fractional ratings draw partly filled items |
| Items | `maxRating`, `itemSize`, `itemSpacing`, `shape` (`StarShape`, `SharpStarShape`, `HeartShape`, `starShape(points, innerRadiusRatio, roundness)`, `CircleShape`…), or `painter` for icons |
| Theming | `ComposeRatingBarDefaults.colors(...)` (from `MaterialTheme`), `accentColors(accent)`, `outlinedColors()` + `borderWidth = OutlineWidth`, `colors.copy(...)` |
| Animation | Items fill in a staggered cascade with a soft bounce, the tapped item pops with a fading halo, the item under your finger lifts while dragging. Tune with `RatingBarAnimation(staggerMillis, waveScale, popScale, dragScale, showHalo)` or turn off with `RatingBarAnimation.None` |
| Haptics | Ticks as the rating changes under the finger (`hapticFeedbackEnabled`) |

Right-to-left layouts fill from the right. Animations run in the draw phase, so the bar doesn't recompose while it moves.

### ComposeOtpInput

A one-time-code field split into cells. Typing fills the cells one by one, backspace clears them, and **pasting** or **SMS autofill** drops a whole code in at once. Characters the input doesn't accept, such as the dash in `482-913`, are skipped.

```kotlin
var code by rememberSaveable { mutableStateOf("") }

ComposeOtpInput(
    value = code,
    onValueChange = { code = it },
    onComplete = { viewModel.verify(it) },     // called when the last cell is filled
    length = 6,
    isError = wrongCode,                       // red cells + a shake
    cellStyle = OtpCellStyle.Outlined,         // Outlined | Filled | Underlined
    groupSize = 3,                             // 123 – 456
)
```

| Feature | How |
| --- | --- |
| Input | `length`, `inputType` (`Digits`, `Letters`, `Alphanumeric`, `Any`), custom `charFilter`, `uppercase`, `enabled`, `readOnly`, `autoFocus`, `clearFocusOnComplete` (closes the keyboard), `keyboardType`, `imeAction`, `keyboardActions` |
| Paste and autofill | A pasted code replaces the old one; the field reports itself as an SMS one-time code to autofill services |
| Masking | `mask`, `maskChar`, `revealMillis` (briefly shows the character just typed) |
| Cells | `cellStyle`, `cellSize` (`Dp.Unspecified` width shares the row's width), `cellSpacing`, `cellShape` (`CircleCellShape` for round cells), `borderWidth`, `focusedBorderWidth`, `textStyle`, `placeholder`, `showCursor` |
| Groups | `groupSize` + `separator` |
| Theming | `ComposeOtpInputDefaults.colors(style, ...)` (from `MaterialTheme`), `accentColors(accent, style)`, `colors.copy(...)` |
| Custom cells | `cellContent = { cell: OtpCell -> ... }` draws each cell from its state (`char`, `text`, `isActive`, `isFilled`, `isError`), e.g. PIN dots |
| Animation | New characters slide in and the cell pops, the cursor blinks, the row shakes on error. Tune with `OtpInputAnimation(slideInText, popScale, blinkCursor, shakeOnError, shakeDistance)` or turn off with `OtpInputAnimation.None` |
| Haptics | A confirm tick when the code is complete and a buzz on error (`hapticFeedbackEnabled`) |

Cells always run left to right, also in right-to-left layouts, since codes are read that way.

## Project structure

| Module | Purpose |
| --- | --- |
| `:components` | The library (`com.nameisjayant.components`), published to Maven Central |
| `:app` | Demo app that showcases each component |

## License

```
Copyright 2026 Jayant Kumar

Licensed under the Apache License, Version 2.0
```
