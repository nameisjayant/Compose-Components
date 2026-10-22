package com.nameisjayant.components.ratingbar

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class RatingMathTest {

    // 5 items of 40px with 10px gaps: item i spans [50i, 50i + 40].
    private fun raw(x: Float) = ratingAtPosition(x, itemSizePx = 40f, spacingPx = 10f, maxRating = 5)

    @Test
    fun positionMapsToRawRating() {
        assertEquals(0f, raw(-12f), 0f)
        assertEquals(0.5f, raw(20f), 1e-4f)
        assertEquals(1f, raw(45f), 1e-4f) // in the gap after the first item
        assertEquals(2.25f, raw(110f), 1e-4f)
        assertEquals(5f, raw(400f), 0f)
    }

    @Test
    fun touchRoundsUpToTheStep() {
        assertEquals(3f, snapRating(2.1f, 1f, 0f, 5, roundUp = true), 0f)
        assertEquals(2.5f, snapRating(2.1f, 0.5f, 0f, 5, roundUp = true), 0f)
        assertEquals(2f, snapRating(2.0000002f, 1f, 0f, 5, roundUp = true), 0f)
        assertEquals(2.37f, snapRating(2.37f, 0f, 0f, 5, roundUp = true), 1e-3f)
    }

    @Test
    fun accessibilityRoundsToNearest() {
        assertEquals(2f, snapRating(2.2f, 1f, 0f, 5, roundUp = false), 0f)
        assertEquals(2.5f, snapRating(2.6f, 0.5f, 0f, 5, roundUp = false), 0f)
    }

    @Test
    fun snappedValueIsClamped() {
        assertEquals(1f, snapRating(0f, 1f, 1f, 5, roundUp = true), 0f)
        assertEquals(5f, snapRating(7f, 0.5f, 0f, 5, roundUp = true), 0f)
    }

    @Test
    fun itemUnderFingerIsClamped() {
        assertEquals(0, itemAtPosition(-30f, 40f, 10f, 5))
        assertEquals(2, itemAtPosition(120f, 40f, 10f, 5))
        assertEquals(4, itemAtPosition(900f, 40f, 10f, 5))
    }

    @Test
    fun fractionPerItem() {
        assertEquals(1f, fractionOf(3.4f, 2), 0f)
        assertEquals(0.4f, fractionOf(3.4f, 3), 1e-4f)
        assertEquals(0f, fractionOf(3.4f, 4), 0f)
    }

    @Test
    fun formatsWholeAndHalfRatings() {
        assertEquals("4", formatRating(4f, Locale.US))
        assertEquals("3.5", formatRating(3.5f, Locale.US))
        assertEquals("3,5", formatRating(3.5f, Locale.GERMANY))
    }
}
