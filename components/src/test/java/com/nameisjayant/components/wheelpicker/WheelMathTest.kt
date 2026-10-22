package com.nameisjayant.components.wheelpicker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WheelMathTest {

    @Test
    fun rawCountIsTheItemsOrWholeCopiesOfThem() {
        assertEquals(0, wheelRawCount(0, cyclic = true))
        assertEquals(12, wheelRawCount(12, cyclic = false))
        val cyclic = wheelRawCount(12, cyclic = true)
        assertEquals(0, cyclic % 12)
        assertTrue(cyclic > Int.MAX_VALUE - 12)
    }

    @Test
    fun startRowShowsTheItemAndCyclicStartsInTheMiddle() {
        assertEquals(3, wheelStartRawIndex(3, 10, cyclic = false))
        assertEquals(9, wheelStartRawIndex(42, 10, cyclic = false))
        val raw = wheelStartRawIndex(3, 10, cyclic = true)
        assertEquals(3, wheelLogicalIndex(raw, 10))
        assertTrue(raw > Int.MAX_VALUE / 4 && raw < Int.MAX_VALUE / 4 * 3)
    }

    @Test
    fun cyclicScrollTakesTheShortWayRound() {
        val start = wheelStartRawIndex(1, 12, cyclic = true)
        // From 1 to 11 is two steps back, not ten forward.
        assertEquals(start - 2, wheelNearestRawIndex(start, 11, 12, cyclic = true))
        assertEquals(start + 4, wheelNearestRawIndex(start, 5, 12, cyclic = true))
        assertEquals(start, wheelNearestRawIndex(start, 1, 12, cyclic = true))
        assertEquals(11, wheelNearestRawIndex(1, 11, 12, cyclic = false))
    }

    @Test
    fun centerRowFlipsHalfwayThroughARow() {
        assertEquals(4, wheelCenterRawIndex(4, 0, 100))
        assertEquals(4, wheelCenterRawIndex(4, 49, 100))
        assertEquals(5, wheelCenterRawIndex(4, 50, 100))
        assertEquals(4, wheelCenterRawIndex(4, 30, 0))
    }

    @Test
    fun nearestEnabledLooksAfterThenBefore() {
        val even = { i: Int -> i % 2 == 0 }
        assertEquals(4, wheelNearestEnabledIndex(4, 10, cyclic = false, isEnabled = even))
        assertEquals(4, wheelNearestEnabledIndex(3, 10, cyclic = false, isEnabled = even))
        // The last item has nothing after it, so it falls back.
        assertEquals(8, wheelNearestEnabledIndex(9, 10, cyclic = false, isEnabled = even))
        // A cyclic wheel wraps from the last item to the first.
        assertEquals(0, wheelNearestEnabledIndex(9, 10, cyclic = true, isEnabled = { it == 0 || it == 6 }))
        assertNull(wheelNearestEnabledIndex(2, 5, cyclic = false, isEnabled = { false }))
        assertNull(wheelNearestEnabledIndex(0, 0, cyclic = true, isEnabled = { true }))
    }

    @Test
    fun curvedRowsBunchTowardsTheCenterAndHideBehindTheDrum() {
        assertEquals(0f, wheelCurveShift(0f, 5), 1e-6f)
        // A row two steps down sits closer than two rows from the center…
        val shift = wheelCurveShift(2f, 5)
        assertTrue(shift < 0f && shift > -1f)
        // …and the shift is symmetric above the center.
        assertEquals(-shift, wheelCurveShift(-2f, 5), 1e-6f)
        assertFalse(wheelRowHidden(3f, 5))
        assertTrue(wheelRowHidden(4f, 5))
    }

    @Test
    fun edgeFractionRunsFromCenterToEdge() {
        assertEquals(0f, wheelEdgeFraction(0f, 5), 0f)
        assertEquals(0.4f, wheelEdgeFraction(-1f, 5), 1e-6f)
        assertEquals(1f, wheelEdgeFraction(9f, 5), 0f)
    }
}
