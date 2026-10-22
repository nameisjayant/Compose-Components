package com.nameisjayant.composecomponents.wheelpicker

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.wheelpicker.ComposeWheelPicker
import com.nameisjayant.components.wheelpicker.ComposeWheelPickerColors
import com.nameisjayant.components.wheelpicker.ComposeWheelPickerDefaults
import com.nameisjayant.components.wheelpicker.ComposeWheelPickerState
import com.nameisjayant.components.wheelpicker.WheelPickerItem
import com.nameisjayant.components.wheelpicker.WheelPickerStyle
import com.nameisjayant.components.wheelpicker.rememberComposeWheelPickerState
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme
import kotlinx.coroutines.launch
import java.util.Calendar

private enum class WheelLook(val label: String, val style: WheelPickerStyle) {
    Wheel("Wheel", WheelPickerStyle.Wheel),
    Flat("Flat", WheelPickerStyle.Flat),
}

private enum class VisibleRows(val label: String, val count: Int) {
    Three("3 rows", 3),
    Five("5 rows", 5),
    Seven("7 rows", 7),
}

private enum class Indicator(val label: String) {
    Band("Band"),
    Lines("Lines"),
    None("None"),
}

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class WheelAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
    Amber("Amber", Color(0xFFB86E00), Color(0xFFFFC233)),
}

private class Drink(val emoji: String, val name: String, val price: String, val soldOut: Boolean = false)

private val Drinks = listOf(
    Drink("☕", "Espresso", "$2.40"),
    Drink("🥛", "Flat white", "$3.60"),
    Drink("🍵", "Matcha latte", "$4.20", soldOut = true),
    Drink("🧋", "Bubble tea", "$4.80"),
    Drink("🍫", "Mocha", "$3.90"),
    Drink("🧊", "Cold brew", "$3.80", soldOut = true),
    Drink("🍋", "Lemonade", "$2.90"),
    Drink("🍓", "Smoothie", "$5.10"),
)

private val Hours = (1..12).map { it.toString() }
private val Minutes = (0..59).map { "%02d".format(it) }
private val Periods = listOf("AM", "PM")
private val Months = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)
private val Years = (1950..2035).toList()

@Composable
fun WheelPickerDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    var look by rememberSaveable { mutableStateOf(WheelLook.Wheel) }
    var rows by rememberSaveable { mutableStateOf(VisibleRows.Five) }
    var indicator by rememberSaveable { mutableStateOf(Indicator.Band) }
    var accent by rememberSaveable { mutableStateOf(WheelAccent.App) }

    // Behaviour
    var loop by rememberSaveable { mutableStateOf(true) }
    var fade by rememberSaveable { mutableStateOf(true) }
    var bold by rememberSaveable { mutableStateOf(true) }
    var tapToSelect by rememberSaveable { mutableStateOf(true) }
    var skipSoldOut by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var haptics by rememberSaveable { mutableStateOf(true) }

    val accentColor = (if (isDark) accent.dark else accent.light) ?: MaterialTheme.colorScheme.primary
    val colors = if (accent == WheelAccent.App) ComposeWheelPickerDefaults.colors() else ComposeWheelPickerDefaults.accentColors(accentColor)
    val textStyle = ComposeWheelPickerDefaults.textStyle()
    val options = WheelOptions(
        style = look.style,
        visibleItemCount = rows.count,
        textStyle = textStyle,
        selectedTextStyle = if (bold) textStyle.copy(fontWeight = FontWeight.SemiBold) else textStyle,
        colors = colors,
        fade = fade,
        tapToSelect = tapToSelect,
        enabled = enabled,
        haptics = haptics,
    )

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "Wheel Picker", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                AlarmCard(options, indicator, loop)

                SectionLabel("Style")
                Segments(WheelLook.entries, look, { look = it }) { it.label }

                SectionLabel("Visible rows")
                Segments(VisibleRows.entries, rows, { rows = it }) { it.label }

                SectionLabel("Selection indicator")
                Segments(Indicator.entries, indicator, { indicator = it }) { it.label }

                SectionLabel("Date")
                BirthdayCard(options, indicator, loop)

                SectionLabel("Custom rows")
                DrinkCard(options, indicator, loop, skipSoldOut)

                SectionLabel("Behaviour")
                DemoCard {
                    SwitchRow("Loop", loop, { loop = it }, hint = "Hours, minutes, days, months and drinks wrap round")
                    Divider()
                    SwitchRow("Fade edges", fade, { fade = it })
                    Divider()
                    SwitchRow("Bold selection", bold, { bold = it }, hint = "selectedTextStyle")
                    Divider()
                    SwitchRow("Tap a row to select it", tapToSelect, { tapToSelect = it })
                    Divider()
                    SwitchRow("Skip sold-out drinks", skipSoldOut, { skipSoldOut = it }, hint = "isItemEnabled")
                    Divider()
                    SwitchRow("Enabled", enabled, { enabled = it })
                    Divider()
                    SwitchRow("Haptics", haptics, { haptics = it }, hint = "A tick for every row that passes")
                }

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        WheelAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

/** The settings every demo wheel shares. */
private class WheelOptions(
    val style: WheelPickerStyle,
    val visibleItemCount: Int,
    val textStyle: TextStyle,
    val selectedTextStyle: TextStyle,
    val colors: ComposeWheelPickerColors,
    val fade: Boolean,
    val tapToSelect: Boolean,
    val enabled: Boolean,
    val haptics: Boolean,
)

/** Hours, minutes and AM/PM driven by [rememberComposeWheelPickerState], with buttons that spin them from code. */
@Composable
private fun AlarmCard(options: WheelOptions, indicator: Indicator, loop: Boolean) {
    val hours = rememberComposeWheelPickerState(initialIndex = 6)
    val minutes = rememberComposeWheelPickerState(initialIndex = 30)
    val period = rememberComposeWheelPickerState(initialIndex = 0)
    val scope = rememberCoroutineScope()

    fun spinTo(hour24: Int, minute: Int) {
        scope.launch { hours.animateScrollToIndex((hour24 + 11) % 12) }
        scope.launch { minutes.animateScrollToIndex(minute) }
        scope.launch { period.animateScrollToIndex(if (hour24 < 12) 0 else 1) }
    }

    DemoCard(Modifier.padding(top = 4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Wake me up at", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            // selectedIndex follows the wheels live while they turn.
            Text(
                text = "${Hours[hours.selectedIndex]}:${Minutes[minutes.selectedIndex]} ${Periods[period.selectedIndex]}",
                style = MaterialTheme.typography.headlineMedium,
                color = options.colors.selectedTextColor,
            )
            Spacer(Modifier.height(16.dp))

            SharedIndicator(indicator, options) {
                RowWheel(
                    items = Hours,
                    state = hours,
                    modifier = Modifier.width(72.dp),
                    cyclic = loop,
                    contentDescription = "Hour",
                    options = options,
                )
                Text(":", style = options.textStyle, color = options.colors.selectedTextColor)
                RowWheel(
                    items = Minutes,
                    state = minutes,
                    modifier = Modifier.width(72.dp),
                    cyclic = loop,
                    contentDescription = "Minute",
                    options = options,
                )
                RowWheel(
                    items = Periods,
                    state = period,
                    modifier = Modifier.width(72.dp),
                    contentDescription = "AM or PM",
                    options = options,
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = {
                        val now = Calendar.getInstance()
                        spinTo(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
                    },
                    enabled = options.enabled,
                ) { Text("Now") }
                TextButton(onClick = { spinTo(7, 0) }, enabled = options.enabled) { Text("7:00 AM") }
                TextButton(onClick = { spinTo(22, 45) }, enabled = options.enabled) { Text("10:45 PM") }
            }
        }
    }
}

/** Day, month and year with plain index state; the day wheel shrinks and grows with the month. */
@Composable
private fun BirthdayCard(options: WheelOptions, indicator: Indicator, loop: Boolean) {
    var day by rememberSaveable { mutableIntStateOf(13) }
    var month by rememberSaveable { mutableIntStateOf(2) }
    var year by rememberSaveable { mutableIntStateOf(Years.indexOf(1995)) }
    val days = remember(month, year) { (1..daysInMonth(month, Years[year])).map { it.toString() } }

    DemoCard {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SharedIndicator(indicator, options) {
                RowWheel(
                    items = days,
                    selectedIndex = day,
                    onSelectedIndexChange = { day = it },
                    modifier = Modifier.width(64.dp),
                    cyclic = loop,
                    contentDescription = "Day",
                    options = options,
                )
                RowWheel(
                    items = Months,
                    selectedIndex = month,
                    onSelectedIndexChange = { month = it },
                    modifier = Modifier.weight(1f),
                    cyclic = loop,
                    contentDescription = "Month",
                    options = options,
                )
                RowWheel(
                    items = Years,
                    selectedIndex = year,
                    onSelectedIndexChange = { year = it },
                    modifier = Modifier.width(88.dp),
                    contentDescription = "Year",
                    options = options,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${(day + 1).coerceAtMost(days.size)} ${Months[month]} ${Years[year]}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Rows drawn through `itemContent`; sold-out drinks are skipped with `isItemEnabled`. */
@Composable
private fun DrinkCard(options: WheelOptions, indicator: Indicator, loop: Boolean, skipSoldOut: Boolean) {
    var drink by rememberSaveable { mutableIntStateOf(1) }

    DemoCard {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ComposeWheelPicker(
                items = Drinks,
                selectedIndex = drink,
                onSelectedIndexChange = { drink = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = options.enabled,
                cyclic = loop,
                visibleItemCount = options.visibleItemCount,
                itemHeight = 56.dp,
                style = options.style,
                colors = options.colors,
                selectionIndicator = indicatorFor(indicator, options.colors),
                fadeEdges = options.fade,
                isItemEnabled = { !skipSoldOut || !Drinks[it].soldOut },
                selectOnTap = options.tapToSelect,
                hapticFeedbackEnabled = options.haptics,
                contentDescription = "Drink",
                itemLabel = { it.name },
            ) { row -> DrinkRow(row) }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Your order: ${Drinks[drink].name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DrinkRow(row: WheelPickerItem<Drink>) {
    val drink = row.value
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(drink.emoji, style = MaterialTheme.typography.titleLarge)
        Text(
            text = drink.name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (row.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textDecoration = if (drink.soldOut) TextDecoration.LineThrough else null,
            ),
            color = LocalContentColor.current,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (drink.soldOut) "Sold out" else drink.price,
            style = MaterialTheme.typography.labelLarge,
            color = LocalContentColor.current,
        )
    }
}

/** Wheels in a row that share one selection indicator drawn behind them. */
@Composable
private fun SharedIndicator(indicator: Indicator, options: WheelOptions, wheels: @Composable RowScope.() -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        indicatorFor(indicator, options.colors)?.let { draw ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(ComposeWheelPickerDefaults.ItemHeight),
            ) { draw() }
        }
        Row(verticalAlignment = Alignment.CenterVertically, content = wheels)
    }
}

private fun indicatorFor(indicator: Indicator, colors: ComposeWheelPickerColors): (@Composable () -> Unit)? = when (indicator) {
    Indicator.Band -> { { ComposeWheelPickerDefaults.SelectionBand(color = colors.indicatorColor) } }
    Indicator.Lines -> { { ComposeWheelPickerDefaults.SelectionLines(color = colors.indicatorLineColor) } }
    Indicator.None -> null
}

/** A row wheel with the shared [options]; its own indicator is off since the row draws one. */
@Composable
private fun <T> RowWheel(
    items: List<T>,
    state: ComposeWheelPickerState,
    modifier: Modifier,
    contentDescription: String,
    options: WheelOptions,
    cyclic: Boolean = false,
) {
    ComposeWheelPicker(
        items = items,
        modifier = modifier,
        state = state,
        enabled = options.enabled,
        cyclic = cyclic,
        visibleItemCount = options.visibleItemCount,
        style = options.style,
        textStyle = options.textStyle,
        selectedTextStyle = options.selectedTextStyle,
        colors = options.colors,
        selectionIndicator = null,
        fadeEdges = options.fade,
        selectOnTap = options.tapToSelect,
        hapticFeedbackEnabled = options.haptics,
        contentDescription = contentDescription,
    )
}

/** Same, driven by a plain index. */
@Composable
private fun <T> RowWheel(
    items: List<T>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier,
    contentDescription: String,
    options: WheelOptions,
    cyclic: Boolean = false,
) {
    ComposeWheelPicker(
        items = items,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = onSelectedIndexChange,
        modifier = modifier,
        enabled = options.enabled,
        cyclic = cyclic,
        visibleItemCount = options.visibleItemCount,
        style = options.style,
        textStyle = options.textStyle,
        selectedTextStyle = options.selectedTextStyle,
        colors = options.colors,
        selectionIndicator = null,
        fadeEdges = options.fade,
        selectOnTap = options.tapToSelect,
        hapticFeedbackEnabled = options.haptics,
        contentDescription = contentDescription,
    )
}

private fun daysInMonth(month: Int, year: Int): Int = when (month) {
    1 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
    3, 5, 8, 10 -> 30
    else -> 31
}

@Composable
private fun <T> Segments(options: List<T>, selected: T, onSelect: (T) -> Unit, label: (T) -> String) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) { Text(label(option), maxLines = 1) }
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

@Preview
@Composable
private fun WheelPickerDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        WheelPickerDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun WheelPickerDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        WheelPickerDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
