package com.nameisjayant.components.datepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/** How taps on days change the selection. */
public enum class DateSelectionMode {
    /** One date. Tapping another date replaces it. */
    Single,

    /** A start and an end date. The first tap sets the start, the second the end. */
    Range,

    /** Any number of dates (up to `maxSelectionCount`). Tapping a selected date removes it. */
    Multiple,
}

/**
 * State of a [ComposeDatePicker]: the selection, the month on screen and the rules for what can be picked.
 *
 * Create it with [rememberComposeDatePickerState] so it survives recomposition and configuration changes.
 * Read [selectedDate], [selectedRange] or [selectedDates] (depending on [selectionMode]) to get the result;
 * they are snapshot state, so composables that read them update automatically.
 *
 * @param selectionMode single date, range or multiple dates.
 * @param minDate earliest date the picker can show or select.
 * @param maxDate latest date the picker can show or select.
 * @param selectableDates extra rule for disabling dates inside [minDate]..[maxDate].
 * @param maxSelectionCount in [DateSelectionMode.Multiple], the most dates that can be selected at once.
 * @param allowDisabledDatesInRange in [DateSelectionMode.Range], whether a range may span disabled dates.
 *   When `false` (the default), tapping an end date past a disabled date starts a new range instead.
 */
@Stable
public class ComposeDatePickerState(
    public val selectionMode: DateSelectionMode = DateSelectionMode.Single,
    initialSelectedDate: CalendarDate? = null,
    initialSelectedRange: ClosedRange<CalendarDate>? = null,
    initialSelectedDates: Set<CalendarDate> = emptySet(),
    initialDisplayedMonth: CalendarMonth? = null,
    public val minDate: CalendarDate = ComposeDatePickerDefaults.MinDate,
    public val maxDate: CalendarDate = ComposeDatePickerDefaults.MaxDate,
    selectableDates: SelectableDates = SelectableDates.All,
    public val maxSelectionCount: Int = Int.MAX_VALUE,
    public val allowDisabledDatesInRange: Boolean = false,
) {
    init {
        require(minDate <= maxDate) { "minDate ($minDate) must not be after maxDate ($maxDate)" }
        require(maxSelectionCount >= 1) { "maxSelectionCount must be at least 1, was $maxSelectionCount" }
    }

    /** Rule for disabling dates. Can be swapped at any time, e.g. when a filter changes. */
    public var selectableDates: SelectableDates by mutableStateOf(selectableDates)

    /** The selected date in [DateSelectionMode.Single], or `null`. */
    public var selectedDate: CalendarDate? by mutableStateOf(initialSelectedDate)

    /** Start of the range in [DateSelectionMode.Range]; set as soon as the first date is tapped. */
    public var rangeStart: CalendarDate? by mutableStateOf(initialSelectedRange?.start)
        private set

    /** End of the range in [DateSelectionMode.Range]; `null` until the second date is tapped. */
    public var rangeEnd: CalendarDate? by mutableStateOf(initialSelectedRange?.endInclusive)
        private set

    /** The complete range, or `null` while it is empty or only has a start. */
    public val selectedRange: ClosedRange<CalendarDate>?
        get() {
            val start = rangeStart ?: return null
            val end = rangeEnd ?: return null
            return start..end
        }

    /** The selected dates in [DateSelectionMode.Multiple], in no particular order. */
    public var selectedDates: Set<CalendarDate> by mutableStateOf(initialSelectedDates)

    private val minMonth get() = minDate.calendarMonth
    private val maxMonth get() = maxDate.calendarMonth

    private var _displayedMonth by mutableStateOf(
        (initialDisplayedMonth
            ?: initialSelectedDate?.calendarMonth
            ?: initialSelectedRange?.start?.calendarMonth
            ?: initialSelectedDates.minOrNull()?.calendarMonth
            ?: CalendarMonth.current()).coerceIn(minDate.calendarMonth, maxDate.calendarMonth),
    )

    /** The month on screen. Setting it scrolls the picker; values are clamped to [minDate]..[maxDate]. */
    public var displayedMonth: CalendarMonth
        get() = _displayedMonth
        set(value) {
            _displayedMonth = value.coerceIn(minMonth, maxMonth)
        }

    /** Whether anything is selected, including a range that only has a start. */
    public val hasSelection: Boolean
        get() = when (selectionMode) {
            DateSelectionMode.Single -> selectedDate != null
            DateSelectionMode.Range -> rangeStart != null
            DateSelectionMode.Multiple -> selectedDates.isNotEmpty()
        }

    /** Whether [date] is inside [minDate]..[maxDate] and allowed by [selectableDates]. */
    public fun isSelectable(date: CalendarDate): Boolean =
        date in minDate..maxDate && selectableDates.isSelectable(date)

    /** Whether [date] is shown as selected: the single date, a range end, or one of the multiple dates. */
    public fun isSelected(date: CalendarDate): Boolean = when (selectionMode) {
        DateSelectionMode.Single -> date == selectedDate
        DateSelectionMode.Range -> date == rangeStart || date == rangeEnd
        DateSelectionMode.Multiple -> date in selectedDates
    }

    /**
     * Applies a tap on [date] the way the picker does. Returns `false` if the tap was ignored
     * (disabled date, or [maxSelectionCount] reached).
     */
    public fun select(date: CalendarDate): Boolean {
        if (!isSelectable(date)) return false
        when (selectionMode) {
            DateSelectionMode.Single -> selectedDate = date

            DateSelectionMode.Range -> {
                val start = rangeStart
                when {
                    start == null || rangeEnd != null || date < start -> setRange(date, null)
                    !allowDisabledDatesInRange && hasDisabledDateBetween(start, date) -> setRange(date, null)
                    else -> rangeEnd = date
                }
            }

            DateSelectionMode.Multiple -> selectedDates = when {
                date in selectedDates -> selectedDates - date
                selectedDates.size >= maxSelectionCount -> return false
                else -> selectedDates + date
            }
        }
        return true
    }

    /**
     * Sets the range directly. [end] may be `null` for a range that only has a start.
     * Throws if [end] is before [start].
     */
    public fun setRange(start: CalendarDate?, end: CalendarDate?) {
        require(start != null || end == null) { "A range with an end needs a start" }
        require(start == null || end == null || start <= end) { "Range end ($end) is before its start ($start)" }
        rangeStart = start
        rangeEnd = end
    }

    /** Clears the selection of the current [selectionMode]. */
    public fun clearSelection() {
        selectedDate = null
        setRange(null, null)
        selectedDates = emptySet()
    }

    private fun hasDisabledDateBetween(start: CalendarDate, end: CalendarDate): Boolean {
        var day = start.plusDays(1)
        while (day < end) {
            if (!isSelectable(day)) return true
            day = day.plusDays(1)
        }
        return false
    }

    public companion object {
        /** Saves everything except [selectableDates], which is code and is passed back in on restore. */
        public fun Saver(selectableDates: SelectableDates): Saver<ComposeDatePickerState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.selectionMode.ordinal,
                    state.selectedDate?.epochDay,
                    state.rangeStart?.epochDay,
                    state.rangeEnd?.epochDay,
                    state.selectedDates.map { it.epochDay }.toLongArray(),
                    state.displayedMonth.firstDay.epochDay,
                    state.minDate.epochDay,
                    state.maxDate.epochDay,
                    state.maxSelectionCount,
                    state.allowDisabledDatesInRange,
                )
            },
            restore = { saved ->
                val start = (saved[2] as Long?)?.let(CalendarDate::ofEpochDay)
                val end = (saved[3] as Long?)?.let(CalendarDate::ofEpochDay)
                ComposeDatePickerState(
                    selectionMode = DateSelectionMode.entries[saved[0] as Int],
                    initialSelectedDate = (saved[1] as Long?)?.let(CalendarDate::ofEpochDay),
                    initialSelectedDates = (saved[4] as LongArray).mapTo(HashSet(), CalendarDate::ofEpochDay),
                    initialDisplayedMonth = CalendarDate.ofEpochDay(saved[5] as Long).calendarMonth,
                    minDate = CalendarDate.ofEpochDay(saved[6] as Long),
                    maxDate = CalendarDate.ofEpochDay(saved[7] as Long),
                    selectableDates = selectableDates,
                    maxSelectionCount = saved[8] as Int,
                    allowDisabledDatesInRange = saved[9] as Boolean,
                ).also { it.setRange(start, end) }
            },
        )
    }
}

/**
 * Creates and remembers a [ComposeDatePickerState] that survives configuration changes.
 *
 * Changing [selectionMode], [minDate], [maxDate], [maxSelectionCount] or [allowDisabledDatesInRange]
 * creates a fresh state (and drops the selection). Changing [selectableDates] keeps the selection.
 */
@Composable
public fun rememberComposeDatePickerState(
    selectionMode: DateSelectionMode = DateSelectionMode.Single,
    initialSelectedDate: CalendarDate? = null,
    initialSelectedRange: ClosedRange<CalendarDate>? = null,
    initialSelectedDates: Set<CalendarDate> = emptySet(),
    initialDisplayedMonth: CalendarMonth? = null,
    minDate: CalendarDate = ComposeDatePickerDefaults.MinDate,
    maxDate: CalendarDate = ComposeDatePickerDefaults.MaxDate,
    selectableDates: SelectableDates = SelectableDates.All,
    maxSelectionCount: Int = Int.MAX_VALUE,
    allowDisabledDatesInRange: Boolean = false,
): ComposeDatePickerState {
    val state = rememberSaveable(
        selectionMode, minDate, maxDate, maxSelectionCount, allowDisabledDatesInRange,
        saver = ComposeDatePickerState.Saver(selectableDates),
    ) {
        ComposeDatePickerState(
            selectionMode = selectionMode,
            initialSelectedDate = initialSelectedDate,
            initialSelectedRange = initialSelectedRange,
            initialSelectedDates = initialSelectedDates,
            initialDisplayedMonth = initialDisplayedMonth,
            minDate = minDate,
            maxDate = maxDate,
            selectableDates = selectableDates,
            maxSelectionCount = maxSelectionCount,
            allowDisabledDatesInRange = allowDisabledDatesInRange,
        )
    }
    if (state.selectableDates !== selectableDates) state.selectableDates = selectableDates
    return state
}
