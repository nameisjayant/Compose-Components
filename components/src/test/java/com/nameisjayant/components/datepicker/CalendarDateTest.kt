package com.nameisjayant.components.datepicker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalendarDateTest {

    @Test
    fun epochDayMatchesJavaTime() {
        var day = LocalDate.of(1890, 1, 1)
        val end = LocalDate.of(2110, 12, 31)
        while (!day.isAfter(end)) {
            val date = CalendarDate(day.year, day.monthValue, day.dayOfMonth)
            assertEquals(day.toEpochDay(), date.epochDay)
            assertEquals(date, CalendarDate.ofEpochDay(day.toEpochDay()))
            assertEquals(day.dayOfWeek.value, date.dayOfWeek.isoNumber)
            day = day.plusDays(1)
        }
    }

    @Test
    fun plusMonthsClampsToMonthEnd() {
        assertEquals(CalendarDate(2024, 2, 29), CalendarDate(2024, 1, 31).plusMonths(1))
        assertEquals(CalendarDate(2025, 2, 28), CalendarDate(2025, 1, 31).plusMonths(1))
        assertEquals(CalendarDate(2025, 12, 15), CalendarDate(2026, 1, 15).minusMonths(1))
    }

    @Test
    fun calendarMonthArithmetic() {
        assertEquals(CalendarMonth(2027, 2), CalendarMonth(2026, 11).plusMonths(3))
        assertEquals(CalendarMonth(2025, 12), CalendarMonth(2026, 1).minusMonths(1))
        assertEquals(13, CalendarMonth(2026, 1).monthsUntil(CalendarMonth(2027, 2)))
        assertTrue(CalendarMonth(2026, 1) < CalendarMonth(2026, 2))
        assertEquals(29, CalendarMonth(2000, 2).lengthOfMonth)
        assertEquals(28, CalendarMonth(1900, 2).lengthOfMonth)
    }

    @Test
    fun parseAndToStringRoundTrip() {
        val date = CalendarDate(2026, 10, 22)
        assertEquals("2026-10-22", date.toString())
        assertEquals(date, CalendarDate.parse("2026-10-22"))
    }

    @Test
    fun epochMillisRoundTrip() {
        val date = CalendarDate(1969, 12, 31)
        assertEquals(-86_400_000L, date.toEpochMillisUtc())
        assertEquals(date, CalendarDate.ofEpochMillisUtc(-1L))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidDay() {
        CalendarDate(2026, 2, 29)
    }

    @Test
    fun weekDayWrapsAround() {
        assertEquals(WeekDay.Monday, WeekDay.Sunday + 1)
        assertEquals(WeekDay.Saturday, WeekDay.Monday + -2)
        assertFalse(CalendarDate(2026, 10, 22).isWeekend)
        assertTrue(CalendarDate(2026, 10, 24).isWeekend)
    }
}
