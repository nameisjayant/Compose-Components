package com.nameisjayant.components.datepicker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ComposeDatePickerStateTest {

    private val oct22 = CalendarDate(2026, 10, 22)

    @Test
    fun singleReplacesSelection() {
        val state = ComposeDatePickerState(DateSelectionMode.Single)
        assertTrue(state.select(oct22))
        assertTrue(state.select(oct22.plusDays(1)))
        assertEquals(oct22.plusDays(1), state.selectedDate)
    }

    @Test
    fun disabledDatesAreIgnored() {
        val state = ComposeDatePickerState(selectableDates = SelectableDates.WeekdaysOnly)
        assertFalse(state.select(CalendarDate(2026, 10, 24)))
        assertNull(state.selectedDate)
    }

    @Test
    fun datesOutsideBoundsAreIgnored() {
        val state = ComposeDatePickerState(minDate = oct22, maxDate = oct22.plusDays(10))
        assertFalse(state.select(oct22.minusDays(1)))
        assertFalse(state.select(oct22.plusDays(11)))
        assertTrue(state.select(oct22.plusDays(10)))
    }

    @Test
    fun rangeFlow() {
        val state = ComposeDatePickerState(DateSelectionMode.Range)
        state.select(oct22)
        assertEquals(oct22, state.rangeStart)
        assertNull(state.selectedRange)

        state.select(oct22.plusDays(6))
        assertEquals(oct22..oct22.plusDays(6), state.selectedRange)

        // A third tap starts over.
        state.select(oct22.plusDays(2))
        assertEquals(oct22.plusDays(2), state.rangeStart)
        assertNull(state.rangeEnd)
    }

    @Test
    fun rangeTapBeforeStartMovesStart() {
        val state = ComposeDatePickerState(DateSelectionMode.Range)
        state.select(oct22)
        state.select(oct22.minusDays(3))
        assertEquals(oct22.minusDays(3), state.rangeStart)
        assertNull(state.rangeEnd)
    }

    @Test
    fun rangeCannotSpanDisabledDatesByDefault() {
        val blocked = oct22.plusDays(2)
        val state = ComposeDatePickerState(
            DateSelectionMode.Range,
            selectableDates = SelectableDates.except(listOf(blocked)),
        )
        state.select(oct22)
        state.select(oct22.plusDays(5))
        assertEquals(oct22.plusDays(5), state.rangeStart)
        assertNull(state.rangeEnd)
    }

    @Test
    fun rangeCanSpanDisabledDatesWhenAllowed() {
        val state = ComposeDatePickerState(
            DateSelectionMode.Range,
            selectableDates = SelectableDates.except(listOf(oct22.plusDays(2))),
            allowDisabledDatesInRange = true,
        )
        state.select(oct22)
        state.select(oct22.plusDays(5))
        assertEquals(oct22..oct22.plusDays(5), state.selectedRange)
    }

    @Test
    fun multipleTogglesAndRespectsLimit() {
        val state = ComposeDatePickerState(DateSelectionMode.Multiple, maxSelectionCount = 2)
        assertTrue(state.select(oct22))
        assertTrue(state.select(oct22.plusDays(1)))
        assertFalse(state.select(oct22.plusDays(2)))
        assertEquals(setOf(oct22, oct22.plusDays(1)), state.selectedDates)

        assertTrue(state.select(oct22))
        assertEquals(setOf(oct22.plusDays(1)), state.selectedDates)
    }

    @Test
    fun displayedMonthIsClampedAndFollowsInitialSelection() {
        val state = ComposeDatePickerState(
            initialSelectedDate = oct22,
            minDate = CalendarDate(2026, 1, 1),
            maxDate = CalendarDate(2026, 12, 31),
        )
        assertEquals(CalendarMonth(2026, 10), state.displayedMonth)
        state.displayedMonth = CalendarMonth(2030, 1)
        assertEquals(CalendarMonth(2026, 12), state.displayedMonth)
    }

    @Test
    fun clearSelectionResetsEverything() {
        val state = ComposeDatePickerState(DateSelectionMode.Range)
        state.select(oct22)
        state.clearSelection()
        assertFalse(state.hasSelection)
    }

    @Test
    fun combinedRules() {
        val rule = SelectableDates.notBefore(oct22) and SelectableDates.WeekdaysOnly
        assertFalse(rule.isSelectable(oct22.minusDays(1)))
        assertFalse(rule.isSelectable(CalendarDate(2026, 10, 24)))
        assertTrue(rule.isSelectable(CalendarDate(2026, 10, 26)))
    }
}
