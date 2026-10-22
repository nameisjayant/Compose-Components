package com.nameisjayant.components.datepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.R
import java.util.Calendar
import java.util.Locale

/** Defaults for [ComposeDatePicker] and [ComposeDatePickerDialog]. */
public object ComposeDatePickerDefaults {

    /** Earliest date a picker allows unless told otherwise. */
    public val MinDate: CalendarDate = CalendarDate(1900, 1, 1)

    /** Latest date a picker allows unless told otherwise. */
    public val MaxDate: CalendarDate = CalendarDate(2100, 12, 31)

    /** Round day cells, as in Material 3. */
    public val DayShape: Shape = CircleShape

    /** Softer, squarer day cells. */
    public val RoundedDayShape: Shape = RoundedCornerShape(12.dp)

    public val DialogShape: Shape = RoundedCornerShape(28.dp)

    /** Colors taken from [MaterialTheme.colorScheme]; any of them can be overridden. */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        headlineContentColor: Color = MaterialTheme.colorScheme.onSurface,
        navigationContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        weekdayContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        weekendContentColor: Color = MaterialTheme.colorScheme.tertiary,
        dayContentColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledDayContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
        adjacentMonthDayContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        todayContentColor: Color = MaterialTheme.colorScheme.primary,
        todayBorderColor: Color = MaterialTheme.colorScheme.primary,
        selectedDayContainerColor: Color = MaterialTheme.colorScheme.primary,
        selectedDayContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        rangeContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        rangeContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        decorationColor: Color = MaterialTheme.colorScheme.primary,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
        yearContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        currentYearContentColor: Color = MaterialTheme.colorScheme.primary,
        selectedYearContainerColor: Color = MaterialTheme.colorScheme.primary,
        selectedYearContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    ): ComposeDatePickerColors = ComposeDatePickerColors(
        containerColor = containerColor,
        titleContentColor = titleContentColor,
        headlineContentColor = headlineContentColor,
        navigationContentColor = navigationContentColor,
        weekdayContentColor = weekdayContentColor,
        weekendContentColor = weekendContentColor,
        dayContentColor = dayContentColor,
        disabledDayContentColor = disabledDayContentColor,
        adjacentMonthDayContentColor = adjacentMonthDayContentColor,
        todayContentColor = todayContentColor,
        todayBorderColor = todayBorderColor,
        selectedDayContainerColor = selectedDayContainerColor,
        selectedDayContentColor = selectedDayContentColor,
        rangeContainerColor = rangeContainerColor,
        rangeContentColor = rangeContentColor,
        decorationColor = decorationColor,
        dividerColor = dividerColor,
        yearContentColor = yearContentColor,
        currentYearContentColor = currentYearContentColor,
        selectedYearContainerColor = selectedYearContainerColor,
        selectedYearContentColor = selectedYearContentColor,
    )

    /**
     * A palette built around one brand color: selection, today, range and decorations use [accent],
     * everything else follows [MaterialTheme], so it works in light and dark themes.
     *
     * @param onAccent text color on top of [accent]; picked from its luminance by default.
     * @param containerColor background of the picker.
     */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(
        accent: Color,
        onAccent: Color = if (accent.luminance() > 0.5f) Color.Black else Color.White,
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ): ComposeDatePickerColors = colors(
        containerColor = containerColor,
        todayContentColor = accent,
        todayBorderColor = accent,
        selectedDayContainerColor = accent,
        selectedDayContentColor = onAccent,
        rangeContainerColor = accent.copy(alpha = 0.18f).compositeOver(containerColor),
        rangeContentColor = MaterialTheme.colorScheme.onSurface,
        decorationColor = accent,
        currentYearContentColor = accent,
        selectedYearContainerColor = accent,
        selectedYearContentColor = onAccent,
    )

    /** The first day of the week for the current locale (Sunday in the US, Monday in most of Europe…). */
    @Composable
    @ReadOnlyComposable
    public fun firstDayOfWeek(): WeekDay =
        WeekDay.fromCalendarDay(Calendar.getInstance(currentLocale()).firstDayOfWeek)

    /** Small label above the headline: "Select date", "Select dates" or "Select range". */
    @Composable
    public fun Title(state: ComposeDatePickerState, modifier: Modifier = Modifier) {
        Text(
            text = stringResource(
                when (state.selectionMode) {
                    DateSelectionMode.Single -> R.string.ccdp_title_single
                    DateSelectionMode.Range -> R.string.ccdp_title_range
                    DateSelectionMode.Multiple -> R.string.ccdp_title_multiple
                },
            ),
            modifier = modifier,
            style = MaterialTheme.typography.labelLarge,
        )
    }

    /** Large text summarising the current selection, e.g. "Thu, Oct 22" or "Oct 22 – Oct 28". */
    @Composable
    public fun Headline(state: ComposeDatePickerState, modifier: Modifier = Modifier) {
        val locale = currentLocale()
        val text = when (state.selectionMode) {
            DateSelectionMode.Single ->
                state.selectedDate?.format(HeadlineSkeleton, locale)
                    ?: stringResource(R.string.ccdp_headline_none)

            DateSelectionMode.Range -> {
                val start = state.rangeStart?.format(RangeSkeleton, locale)
                    ?: stringResource(R.string.ccdp_range_start)
                val end = state.rangeEnd?.format(RangeSkeleton, locale)
                    ?: stringResource(R.string.ccdp_range_end)
                "$start – $end"
            }

            DateSelectionMode.Multiple -> {
                val dates = state.selectedDates
                when (dates.size) {
                    0 -> stringResource(R.string.ccdp_headline_none_multiple)
                    1 -> dates.first().format(HeadlineSkeleton, locale)
                    else -> pluralStringResource(R.plurals.ccdp_headline_count, dates.size, dates.size)
                }
            }
        }
        Column(modifier) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    internal const val HeadlineSkeleton = "EEEMMMd"
    internal const val RangeSkeleton = "MMMd"
}

@Composable
@ReadOnlyComposable
internal fun currentLocale(): Locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
