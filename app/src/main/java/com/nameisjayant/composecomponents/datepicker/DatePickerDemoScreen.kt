package com.nameisjayant.composecomponents.datepicker

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.datepicker.CalendarDate
import com.nameisjayant.components.datepicker.ComposeDatePicker
import com.nameisjayant.components.datepicker.ComposeDatePickerColors
import com.nameisjayant.components.datepicker.ComposeDatePickerDefaults
import com.nameisjayant.components.datepicker.ComposeDatePickerDialog
import com.nameisjayant.components.datepicker.ComposeDatePickerState
import com.nameisjayant.components.datepicker.DateSelectionMode
import com.nameisjayant.components.datepicker.DayDecoration
import com.nameisjayant.components.datepicker.SelectableDates
import com.nameisjayant.components.datepicker.WeekDay
import com.nameisjayant.components.datepicker.and
import com.nameisjayant.components.datepicker.format
import com.nameisjayant.components.datepicker.rememberComposeDatePickerState
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.CardShape
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class PickerAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Amber("Amber", Color(0xFFB87800), Color(0xFFF5B83D)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
    Graphite("Graphite", Color(0xFF2B2B30), Color(0xFFE4E4E7)),
}

@Composable
fun DatePickerDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    val today = remember { CalendarDate.today() }

    // Selection
    var mode by rememberSaveable { mutableStateOf(DateSelectionMode.Single) }
    var limitMultiple by rememberSaveable { mutableStateOf(false) }
    var allowSpanDisabled by rememberSaveable { mutableStateOf(false) }

    // Disabled dates
    var disablePast by rememberSaveable { mutableStateOf(false) }
    var disableWeekends by rememberSaveable { mutableStateOf(false) }
    var blockSomeDates by rememberSaveable { mutableStateOf(true) }
    var strikeDisabled by rememberSaveable { mutableStateOf(false) }

    // Decorations
    var eventDots by rememberSaveable { mutableStateOf(true) }
    var priceLabels by rememberSaveable { mutableStateOf(false) }
    var tintedDays by rememberSaveable { mutableStateOf(false) }

    // Appearance
    var accent by rememberSaveable { mutableStateOf(PickerAccent.App) }
    var roundedDays by rememberSaveable { mutableStateOf(false) }
    var showAdjacent by rememberSaveable { mutableStateOf(false) }
    var highlightWeekends by rememberSaveable { mutableStateOf(false) }
    var mondayFirst by rememberSaveable { mutableStateOf(false) }
    var showHeadline by rememberSaveable { mutableStateOf(true) }

    // Dialog
    var dialogOpen by rememberSaveable { mutableStateOf(false) }
    var dialogResult by rememberSaveable { mutableStateOf<String?>(null) }

    val blockedDates = remember(today) { listOf(2L, 9L, 16L, 23L).map(today::plusDays) }
    val selectableDates = remember(disablePast, disableWeekends, blockSomeDates, today) {
        var rule = SelectableDates.All
        if (disablePast) rule = rule and SelectableDates.notBefore(today)
        if (disableWeekends) rule = rule and SelectableDates.WeekdaysOnly
        if (blockSomeDates) rule = rule and SelectableDates.except(blockedDates)
        rule
    }

    val state = rememberComposeDatePickerState(
        selectionMode = mode,
        maxSelectionCount = if (limitMultiple) 5 else Int.MAX_VALUE,
        allowDisabledDatesInRange = allowSpanDisabled,
        selectableDates = selectableDates,
    )

    val dotColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
    val tint = MaterialTheme.colorScheme.tertiary
    val decoration: ((CalendarDate) -> DayDecoration?)? =
        remember(eventDots, priceLabels, tintedDays, dotColors, tint) {
            if (!eventDots && !priceLabels && !tintedDays) {
                null
            } else {
                { date -> demoDecoration(date, eventDots, priceLabels, tintedDays, dotColors, tint) }
            }
        }

    val accentColor = if (isDark) accent.dark else accent.light
    val colors: ComposeDatePickerColors = if (accentColor == null) {
        ComposeDatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    } else {
        ComposeDatePickerDefaults.accentColors(accentColor, containerColor = MaterialTheme.colorScheme.surface)
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "Date Picker", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)) {
                    DateSelectionMode.entries.forEachIndexed { index, entry ->
                        SegmentedButton(
                            selected = mode == entry,
                            onClick = { mode = entry },
                            shape = SegmentedButtonDefaults.itemShape(index, DateSelectionMode.entries.size),
                            icon = {},
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            ),
                        ) { Text(entry.name) }
                    }
                }

                ComposeDatePicker(
                    state = state,
                    colors = colors,
                    shape = CardShape,
                    dayShape = if (roundedDays) ComposeDatePickerDefaults.RoundedDayShape else ComposeDatePickerDefaults.DayShape,
                    firstDayOfWeek = if (mondayFirst) WeekDay.Monday else ComposeDatePickerDefaults.firstDayOfWeek(),
                    showAdjacentMonthDays = showAdjacent,
                    highlightWeekends = highlightWeekends,
                    strikeThroughDisabledDays = strikeDisabled,
                    decoration = decoration,
                    title = if (showHeadline) ({ ComposeDatePickerDefaults.Title(state) }) else null,
                    headline = if (showHeadline) ({ ComposeDatePickerDefaults.Headline(state) }) else null,
                    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CardShape),
                )

                Spacer(Modifier.height(12.dp))
                SelectionSummary(state)

                SectionLabel("Selection")
                DemoCard {
                    SwitchRow(
                        title = "Limit to 5 dates",
                        hint = "Multiple mode: extra taps are ignored",
                        checked = limitMultiple,
                        onCheckedChange = { limitMultiple = it },
                        enabled = mode == DateSelectionMode.Multiple,
                    )
                    Divider()
                    SwitchRow(
                        title = "Ranges may cross disabled days",
                        hint = "Range mode: otherwise a new range starts",
                        checked = allowSpanDisabled,
                        onCheckedChange = { allowSpanDisabled = it },
                        enabled = mode == DateSelectionMode.Range,
                    )
                }

                SectionLabel("Disabled dates")
                DemoCard {
                    SwitchRow("Disable past dates", disablePast, { disablePast = it })
                    Divider()
                    SwitchRow("Disable weekends", disableWeekends, { disableWeekends = it })
                    Divider()
                    SwitchRow(
                        title = "Block specific dates",
                        hint = "Today + 2, 9, 16 and 23 days",
                        checked = blockSomeDates,
                        onCheckedChange = { blockSomeDates = it },
                    )
                    Divider()
                    SwitchRow("Strike through disabled", strikeDisabled, { strikeDisabled = it })
                }

                SectionLabel("Decoration")
                DemoCard {
                    SwitchRow("Event dots", eventDots, { eventDots = it }, hint = "Up to three colored dots")
                    Divider()
                    SwitchRow("Price labels", priceLabels, { priceLabels = it }, hint = "Small text under the number")
                    Divider()
                    SwitchRow("Tinted days", tintedDays, { tintedDays = it }, hint = "The 1st and 15th of each month")
                }

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        PickerAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                    Divider()
                    SwitchRow("Rounded-square days", roundedDays, { roundedDays = it })
                    Divider()
                    SwitchRow("Show headline", showHeadline, { showHeadline = it })
                    Divider()
                    SwitchRow("Show adjacent month days", showAdjacent, { showAdjacent = it })
                    Divider()
                    SwitchRow("Highlight weekends", highlightWeekends, { highlightWeekends = it })
                    Divider()
                    SwitchRow("Week starts on Monday", mondayFirst, { mondayFirst = it })
                }

                SectionLabel("Dialog")
                DemoCard {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = "The same picker inside ComposeDatePickerDialog, with confirm and cancel actions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (dialogResult != null) {
                            Spacer(Modifier.height(12.dp))
                            Text("Picked: $dialogResult", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { dialogOpen = true }) { Text("Open dialog") }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }

    if (dialogOpen) {
        val dialogState = rememberComposeDatePickerState(selectableDates = selectableDates)
        ComposeDatePickerDialog(
            onDismissRequest = { dialogOpen = false },
            containerColor = colors.containerColor,
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogResult = dialogState.selectedDate?.format("yMMMd")
                        dialogOpen = false
                    },
                    enabled = dialogState.selectedDate != null,
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { dialogOpen = false }) { Text("Cancel") } },
        ) {
            ComposeDatePicker(state = dialogState, colors = colors, decoration = decoration)
        }
    }
}

@Composable
private fun SelectionSummary(state: ComposeDatePickerState) {
    val text = when (state.selectionMode) {
        DateSelectionMode.Single -> state.selectedDate?.format("yMMMMEEEEd") ?: "Tap a day to select it"
        DateSelectionMode.Range -> {
            val start = state.rangeStart
            val end = state.rangeEnd
            when {
                start == null -> "Tap a start day"
                end == null -> "From ${start.format("yMMMd")} — now tap an end day"
                else -> {
                    val nights = start.daysUntil(end)
                    "${start.format("MMMd")} → ${end.format("yMMMd")}  ·  ${nights + 1} days"
                }
            }
        }

        DateSelectionMode.Multiple -> {
            val dates = state.selectedDates.sorted()
            if (dates.isEmpty()) "Tap days to add or remove them"
            else dates.joinToString(", ") { it.format("MMMd") }
        }
    }
    DemoCard(Modifier.animateContentSize()) {
        Row(Modifier.padding(start = 20.dp, end = 8.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
                Text(
                    text = "RESULT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(text, style = MaterialTheme.typography.bodyLarge)
            }
            TextButton(onClick = state::clearSelection, enabled = state.hasSelection) { Text("Clear") }
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

/** Deterministic sample data so the decorations look the same on every run. */
private fun demoDecoration(
    date: CalendarDate,
    eventDots: Boolean,
    priceLabels: Boolean,
    tintedDays: Boolean,
    dotColors: List<Color>,
    tint: Color,
): DayDecoration? {
    val day = date.dayOfMonth
    val dots = when {
        !eventDots -> emptyList()
        day % 9 == 0 -> dotColors + Color(0xFF16A34A)
        day % 6 == 0 -> dotColors
        day % 4 == 0 -> listOf(dotColors.first())
        else -> emptyList()
    }
    val price = if (priceLabels) "$" + (79 + (date.epochDay * 37).mod(60L)) else null
    val tinted = tintedDays && (day == 1 || day == 15)
    if (dots.isEmpty() && price == null && !tinted) return null
    return DayDecoration(
        dots = dots,
        label = price,
        containerColor = if (tinted) tint.copy(alpha = 0.14f) else Color.Unspecified,
        contentColor = if (tinted) tint else Color.Unspecified,
    )
}

@Preview
@Composable
private fun DatePickerDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        DatePickerDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun DatePickerDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        DatePickerDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
