package com.nameisjayant.components.datepicker

import androidx.compose.runtime.Stable

/**
 * Decides which dates can be picked. Dates it rejects are drawn disabled and ignore taps.
 *
 * Combine rules with [and] / [or], e.g. `SelectableDates.notBefore(today) and SelectableDates.WeekdaysOnly`.
 * Remember the instance (or keep it in a `val`) so the picker doesn't recompose every day cell on each
 * recomposition of the caller.
 */
@Stable
public fun interface SelectableDates {

    public fun isSelectable(date: CalendarDate): Boolean

    public companion object {
        /** Every date can be picked. */
        public val All: SelectableDates = SelectableDates { true }

        /** Monday to Friday only. */
        public val WeekdaysOnly: SelectableDates = SelectableDates { !it.isWeekend }

        /** Saturday and Sunday only. */
        public val WeekendsOnly: SelectableDates = SelectableDates { it.isWeekend }

        /** [date] and every date after it. */
        public fun notBefore(date: CalendarDate): SelectableDates = SelectableDates { it >= date }

        /** [date] and every date before it. */
        public fun notAfter(date: CalendarDate): SelectableDates = SelectableDates { it <= date }

        /** Everything except [dates]. */
        public fun except(dates: Collection<CalendarDate>): SelectableDates {
            val blocked = dates.toHashSet()
            return SelectableDates { it !in blocked }
        }

        /** Everything except the given days of the week. */
        public fun exceptDaysOfWeek(vararg days: WeekDay): SelectableDates {
            val blocked = days.toSet()
            return SelectableDates { it.dayOfWeek !in blocked }
        }
    }
}

/** A date is selectable only if both rules allow it. */
public infix fun SelectableDates.and(other: SelectableDates): SelectableDates =
    SelectableDates { isSelectable(it) && other.isSelectable(it) }

/** A date is selectable if either rule allows it. */
public infix fun SelectableDates.or(other: SelectableDates): SelectableDates =
    SelectableDates { isSelectable(it) || other.isSelectable(it) }
