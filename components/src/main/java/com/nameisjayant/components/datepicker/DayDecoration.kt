package com.nameisjayant.components.datepicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Extra marks drawn on a day: event dots, a small label (a price, a count), a tinted background or a
 * different number color. Return one from the picker's `decoration` lambda for the days that need it.
 *
 * Every part is optional; combine them freely.
 *
 * @property dots colors of the dots under the day number. Up to three are drawn.
 *   [Color.Unspecified] uses [ComposeDatePickerColors.decorationColor].
 * @property label short text under the day number, e.g. `"$89"` or `"3"`. Keep it to a few characters.
 * @property labelColor color of [label]. [Color.Unspecified] uses the day's content color.
 * @property containerColor background behind the day when it isn't selected, e.g. to mark holidays.
 * @property contentColor color of the day number when it isn't selected.
 */
@Immutable
public class DayDecoration(
    public val dots: List<Color> = emptyList(),
    public val label: String? = null,
    public val labelColor: Color = Color.Unspecified,
    public val containerColor: Color = Color.Unspecified,
    public val contentColor: Color = Color.Unspecified,
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is DayDecoration && dots == other.dots && label == other.label &&
            labelColor == other.labelColor && containerColor == other.containerColor &&
            contentColor == other.contentColor)

    override fun hashCode(): Int {
        var result = dots.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + labelColor.hashCode()
        result = 31 * result + containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        return result
    }

    public companion object {
        /** A single dot, e.g. "has events". */
        public fun dot(color: Color = Color.Unspecified): DayDecoration = DayDecoration(dots = listOf(color))
    }
}

/**
 * Everything the picker knows about one day cell, passed to a custom `dayContent`.
 *
 * @property isCurrentMonth `false` for leading/trailing days of the neighbouring months.
 * @property isEnabled whether the day can be tapped.
 * @property isSelected whether the day is the single date, a range end, or one of the multiple dates.
 * @property isInRange whether the day lies inside the selected range (including both ends).
 */
@Immutable
public class DayInfo internal constructor(
    public val date: CalendarDate,
    public val isCurrentMonth: Boolean,
    public val isEnabled: Boolean,
    public val isSelected: Boolean,
    public val isToday: Boolean,
    public val isRangeStart: Boolean,
    public val isRangeEnd: Boolean,
    public val isInRange: Boolean,
    public val decoration: DayDecoration?,
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is DayInfo && date == other.date && isCurrentMonth == other.isCurrentMonth &&
            isEnabled == other.isEnabled && isSelected == other.isSelected && isToday == other.isToday &&
            isRangeStart == other.isRangeStart && isRangeEnd == other.isRangeEnd &&
            isInRange == other.isInRange && decoration == other.decoration)

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + isCurrentMonth.hashCode()
        result = 31 * result + isEnabled.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + isToday.hashCode()
        result = 31 * result + isRangeStart.hashCode()
        result = 31 * result + isRangeEnd.hashCode()
        result = 31 * result + isInRange.hashCode()
        result = 31 * result + decoration.hashCode()
        return result
    }
}
