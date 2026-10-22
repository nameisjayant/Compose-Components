package com.nameisjayant.components.datepicker

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formats this date for [locale] using a date skeleton such as `"yMMMd"` (→ "Oct 22, 2026" in English,
 * "22 Oct 2026" in British English). The skeleton lists the fields you want; the locale decides their
 * order and punctuation.
 */
public fun CalendarDate.format(skeleton: String, locale: Locale = Locale.getDefault()): String {
    val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
    return SimpleDateFormat(pattern, locale)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(toEpochMillisUtc()))
}

/** Formats the first day of this month with a skeleton such as `"MMMMy"` (→ "October 2026"). */
public fun CalendarMonth.format(skeleton: String, locale: Locale = Locale.getDefault()): String =
    firstDay.format(skeleton, locale)

/** Short weekday names for [locale], e.g. "Mo", "Tu"…, keyed by [WeekDay]. */
internal fun weekdayNames(locale: Locale): Map<WeekDay, String> {
    val symbols = android.icu.text.DateFormatSymbols.getInstance(locale)
    val names = symbols.getWeekdays(
        android.icu.text.DateFormatSymbols.STANDALONE,
        android.icu.text.DateFormatSymbols.SHORT,
    )
    // ICU indexes by java.util.Calendar constants: SUNDAY = 1 … SATURDAY = 7.
    return WeekDay.entries.associateWith { day ->
        names[if (day == WeekDay.Sunday) 1 else day.isoNumber + 1]
    }
}

/** Full, spoken form of the date for accessibility, e.g. "Thursday, October 22, 2026". */
internal fun CalendarDate.spokenLabel(locale: Locale): String = format("yMMMMEEEEd", locale)
