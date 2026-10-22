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
