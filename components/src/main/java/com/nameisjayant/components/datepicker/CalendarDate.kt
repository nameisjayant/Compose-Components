package com.nameisjayant.components.datepicker

import androidx.compose.runtime.Immutable
import java.util.Calendar
import java.util.TimeZone

/**
 * A calendar date without a time or time zone, e.g. `2026-10-22`.
 *
 * This is a small, dependency-free stand-in for `java.time.LocalDate`, so the library works on
 * minSdk 24 without core library desugaring.
 *
 * @property year the proleptic Gregorian year.
 * @property month the month of the year, from 1 (January) to 12 (December).
 * @property dayOfMonth the day of the month, from 1 to [CalendarMonth.lengthOfMonth].
 */
@Immutable
public data class CalendarDate(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
) : Comparable<CalendarDate> {

    init {
        require(month in 1..12) { "month must be in 1..12, was $month" }
        require(dayOfMonth in 1..lengthOfMonth(year, month)) {
            "dayOfMonth must be in 1..${lengthOfMonth(year, month)} for $year-$month, was $dayOfMonth"
        }
    }

    /** Days since 1970-01-01. */
    public val epochDay: Long get() = daysFromCivil(year, month, dayOfMonth)

    public val dayOfWeek: WeekDay get() = WeekDay.fromIsoNumber((epochDay + 3).mod(7L).toInt() + 1)

    public val calendarMonth: CalendarMonth get() = CalendarMonth(year, month)

    public val isWeekend: Boolean get() = dayOfWeek == WeekDay.Saturday || dayOfWeek == WeekDay.Sunday

    public fun plusDays(days: Long): CalendarDate = ofEpochDay(epochDay + days)

    public fun minusDays(days: Long): CalendarDate = ofEpochDay(epochDay - days)

    /** Adds [months], clamping the day to the last day of the resulting month (Jan 31 + 1 = Feb 28). */
    public fun plusMonths(months: Int): CalendarDate {
        val target = calendarMonth.plusMonths(months)
        return CalendarDate(target.year, target.month, minOf(dayOfMonth, target.lengthOfMonth))
    }

    public fun minusMonths(months: Int): CalendarDate = plusMonths(-months)

    /** Number of days from this date to [other]; negative when [other] is earlier. */
    public fun daysUntil(other: CalendarDate): Long = other.epochDay - epochDay

    /** Midnight UTC of this date, in milliseconds. Matches the values Material 3's `DatePicker` uses. */
    public fun toEpochMillisUtc(): Long = epochDay * MillisPerDay

    override fun compareTo(other: CalendarDate): Int = epochDay.compareTo(other.epochDay)

    override fun toString(): String =
        "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"

    public companion object {
        public fun ofEpochDay(epochDay: Long): CalendarDate = civilFromDays(epochDay)

        /** The date of [epochMillis] in UTC, e.g. a selection from Material 3's `DatePicker`. */
        public fun ofEpochMillisUtc(epochMillis: Long): CalendarDate =
            ofEpochDay(epochMillis.floorDiv(MillisPerDay))

        /** The date [calendar] currently points at, in its own time zone. */
        public fun from(calendar: Calendar): CalendarDate = CalendarDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
        )

        /** Today's date in [timeZone]. */
        public fun today(timeZone: TimeZone = TimeZone.getDefault()): CalendarDate =
            from(Calendar.getInstance(timeZone))

        /** Parses an ISO `yyyy-MM-dd` string, the same format [toString] produces. */
        public fun parse(text: String): CalendarDate {
            val parts = text.trim().split('-')
            require(parts.size == 3) { "Expected yyyy-MM-dd, was \"$text\"" }
            return CalendarDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}

/** A year and month, e.g. `2026-10`. Used for the month the picker shows. */
@Immutable
public data class CalendarMonth(
    val year: Int,
    val month: Int,
) : Comparable<CalendarMonth> {

    init {
        require(month in 1..12) { "month must be in 1..12, was $month" }
    }

    public val lengthOfMonth: Int get() = lengthOfMonth(year, month)

    public val firstDay: CalendarDate get() = CalendarDate(year, month, 1)

    public val lastDay: CalendarDate get() = CalendarDate(year, month, lengthOfMonth)

    public fun plusMonths(months: Int): CalendarMonth {
        val total = year * 12L + (month - 1) + months
        return CalendarMonth(total.floorDiv(12L).toInt(), total.mod(12L).toInt() + 1)
    }

    public fun minusMonths(months: Int): CalendarMonth = plusMonths(-months)

    /** Number of months from this month to [other]; negative when [other] is earlier. */
    public fun monthsUntil(other: CalendarMonth): Int = (other.year - year) * 12 + (other.month - month)

    public operator fun contains(date: CalendarDate): Boolean = date.year == year && date.month == month

    override fun compareTo(other: CalendarMonth): Int = monthsUntil(other).let { -it.coerceIn(-1, 1) }

    override fun toString(): String = "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}"

    public companion object {
        public fun current(timeZone: TimeZone = TimeZone.getDefault()): CalendarMonth =
            CalendarDate.today(timeZone).calendarMonth
    }
}

/** Days of the week, in ISO-8601 order (Monday first). */
public enum class WeekDay {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

    /** 1 for Monday through 7 for Sunday. */
    public val isoNumber: Int get() = ordinal + 1

    public operator fun plus(days: Int): WeekDay = entries[(ordinal + days).mod(7)]

    public companion object {
        public fun fromIsoNumber(isoNumber: Int): WeekDay {
            require(isoNumber in 1..7) { "isoNumber must be in 1..7, was $isoNumber" }
            return entries[isoNumber - 1]
        }

        /** Converts a `java.util.Calendar` day constant (`Calendar.SUNDAY` = 1) to a [WeekDay]. */
        public fun fromCalendarDay(calendarDay: Int): WeekDay =
            if (calendarDay == Calendar.SUNDAY) Sunday else fromIsoNumber(calendarDay - 1)
    }
}

internal const val MillisPerDay: Long = 86_400_000L

internal fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

internal fun lengthOfMonth(year: Int, month: Int): Int = when (month) {
    2 -> if (isLeapYear(year)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

// Howard Hinnant's days_from_civil / civil_from_days: exact for the whole proleptic Gregorian calendar.
private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
    val y = (if (month <= 2) year - 1 else year).toLong()
    val era = (if (y >= 0) y else y - 399) / 400
    val yearOfEra = y - era * 400
    val dayOfYear = (153 * (if (month > 2) month - 3 else month + 9) + 2) / 5 + day - 1
    val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
    return era * 146_097 + dayOfEra - 719_468
}

private fun civilFromDays(epochDay: Long): CalendarDate {
    val z = epochDay + 719_468
    val era = (if (z >= 0) z else z - 146_096) / 146_097
    val dayOfEra = z - era * 146_097
    val yearOfEra = (dayOfEra - dayOfEra / 1_460 + dayOfEra / 36_524 - dayOfEra / 146_096) / 365
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val mp = (5 * dayOfYear + 2) / 153
    val day = (dayOfYear - (153 * mp + 2) / 5 + 1).toInt()
    val month = (if (mp < 10) mp + 3 else mp - 9).toInt()
    val year = (yearOfEra + era * 400 + if (month <= 2) 1 else 0).toInt()
    return CalendarDate(year, month, day)
}
