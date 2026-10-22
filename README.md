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

### ComposeWheelPicker

A scrolling wheel for picking one item from a list: numbers, times, months, anything. **Drag** or **fling** it and it snaps so one row rests on the selection line; **tap** a row to spin to it.

```kotlin
// Plain index state
var month by rememberSaveable { mutableIntStateOf(0) }
ComposeWheelPicker(
    items = months,
    selectedIndex = month,
    onSelectedIndexChange = { month = it },    // called once the wheel comes to rest
)

// Or a state object: live selection while it turns, and spinning from code
val minutes = rememberComposeWheelPickerState(initialIndex = 30)
ComposeWheelPicker(items = (0..59).map { "%02d".format(it) }, state = minutes, cyclic = true)
minutes.selectedIndex                          // follows the wheel live
scope.launch { minutes.animateScrollToIndex(0) }
```

| Feature | How |
| --- | --- |
| Items | Any `List<T>`, labelled by `itemLabel`; the list may change (e.g. days in a month) and the wheel keeps its item, or the last one if the list got shorter |
| Looping | `cyclic = true` wraps the list so the wheel spins endlessly; scrolling from code takes the short way round |
| Layout | `visibleItemCount` (odd), `itemHeight`; width from `modifier` (defaults to `ComposeWheelPickerDefaults.Width`) |
| Look | `style` — `WheelPickerStyle.Wheel` (rows curve round a drum) or `Flat`, or `WheelPickerStyle(curved, outerAlpha, outerScale)`; `fadeEdges`; `textStyle` / `selectedTextStyle` |
| Selection indicator | `selectionIndicator` slot — `ComposeWheelPickerDefaults.SelectionBand()` (default), `SelectionLines()`, your own, or `null` |
| Disabled items | `isItemEnabled = { index -> ... }`: those rows are dimmed and the wheel moves on to the nearest enabled one |
| Input | `enabled`, `selectOnTap`, `flingBehavior` |
| Theming | `ComposeWheelPickerDefaults.colors(...)` (from `MaterialTheme`), `accentColors(accent)`, `colors.copy(...)` |
| Custom rows | `itemContent = { item: WheelPickerItem<T> -> ... }` draws each row from its state (`index`, `value`, `isSelected`, `isEnabled`); rows still curve and fade, and get the right `LocalContentColor` |
| Haptics | A light tick each time a row passes the selection line (`hapticFeedbackEnabled`) |
| Accessibility | One adjustable control: screen readers read `contentDescription` and the selected label, and swipe up/down to change it |

To make one picker out of several wheels (hours : minutes : AM/PM), put them in a `Row` with `selectionIndicator = null` and draw a single `ComposeWheelPickerDefaults.SelectionBand()` behind the row, one `itemHeight` tall. Rows move in the draw phase, so the wheel only recomposes when the selected row changes.

### ComposeTimeline

Steps joined by a line: order tracking, onboarding progress, a history of events. Each step gets an **indicator** on the line, **content** beside it and, optionally, **opposite content** on the other side (a time, say). The line is drawn as reached up to the furthest step that has been reached, and grows into the next step when it is.

```kotlin
ComposeTimeline(
    items = orderSteps,
    stepState = { index, _ -> TimelineStepState.forProgress(index, currentStep) },
    pendingLineStyle = TimelineLineStyle.Dashed,          // line after the current step
    oppositeContent = { step -> Text(step.item.time) },
) { step ->
    Text(step.item.title)
}

// Long timelines: one lazy item per step, mixed with other items
LazyColumn {
    item { Header() }
    timelineItems(events, key = { it.id }) { step -> EventCard(step.item) }
}
```

| Feature | How |
| --- | --- |
| Step states | `stepState` returns `TimelineStepState.Completed`, `Active`, `Upcoming` or `Error` per step; `TimelineStepState.forProgress(index, currentIndex)` derives them from a current step |
| Layout | `orientation` (`Vertical` / `Horizontal`), `contentSide` (`Start`, `End`, `Alternate`), `indicatorAlignment` (`Start` / `Center`), `indicatorSize`, `itemSpacing`, `contentSpacing`, `lineGap`, `oppositeContentFraction`, `itemWidth` (horizontal; `Dp.Unspecified` shares the width) |
| Indicators | `indicator` slot with `StatusIndicator(state)` (default: ring, pulsing dot, check, exclamation), `NumberIndicator(text, state)`, `IconIndicator(icon, state)`, or your own; the slot's scope carries the timeline's `colors`, `indicatorSize` and `animation` |
| Line | `lineStyle` (reached part) and `pendingLineStyle` (the rest): `TimelineLineStyle.Solid`, `Dashed`, `Dotted`, or `TimelineLineStyle(width, dashLength, gapLength, cap)` |
| Content | `content` and `oppositeContent` get a `TimelineStep` (`index`, `item`, `state`, `count`, `contentSide`, `isFirst`, `isLast`) |
| Lazy | `LazyListScope.timelineItems(...)` for a `LazyColumn`, same parameters (vertical) |
| Theming | `ComposeTimelineDefaults.colors(...)` (from `MaterialTheme`), `accentColors(accent)`, `colors.copy(...)` |
| Animation | Steps fade in one after another, the line fills into a newly reached step (and draws back), indicators blend between states, the active step pulses. Tune with `TimelineAnimation(revealItems, staggerMillis, lineFillMillis, pulseActive)` or turn off with `TimelineAnimation.None` |
| Accessibility | Each step reads as one item with its state ("Completed", "In progress"...) and its place in the list |

Horizontal timelines line their tracks up however tall the content above them is, and right-to-left layouts mirror everything. `key` keeps each step's animations when items are added or moved. Lines and pulses animate in the draw phase.

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
