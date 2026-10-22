package com.nameisjayant.components.timeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimelineMathTest {

    @Test
    fun progressGivesCompletedActiveUpcoming() {
        assertEquals(TimelineStepState.Completed, TimelineStepState.forProgress(0, 2))
        assertEquals(TimelineStepState.Active, TimelineStepState.forProgress(2, 2))
        assertEquals(TimelineStepState.Upcoming, TimelineStepState.forProgress(3, 2))
    }

    @Test
    fun alternateStartsAtTheEnd() {
        assertEquals(TimelineContentSide.End, contentSideFor(0, TimelineContentSide.Alternate))
        assertEquals(TimelineContentSide.Start, contentSideFor(1, TimelineContentSide.Alternate))
        assertEquals(TimelineContentSide.End, contentSideFor(2, TimelineContentSide.Alternate))
        assertEquals(TimelineContentSide.Start, contentSideFor(1, TimelineContentSide.Start))
    }

    @Test
    fun connectorIsReachedOnlyWhenBothEndsAre() {
        assertTrue(connectorReached(TimelineStepState.Completed, TimelineStepState.Active))
        assertTrue(connectorReached(TimelineStepState.Completed, TimelineStepState.Error))
        assertFalse(connectorReached(TimelineStepState.Active, TimelineStepState.Upcoming))
        assertFalse(connectorReached(TimelineStepState.Upcoming, TimelineStepState.Completed))
    }

    @Test
    fun oneSidedContentTakesTheRestOfTheWidth() {
        // 300 wide, 24 track, 16 gap: content after the track gets 300 - 24 - 16.
        assertEquals(0 to 260, sideWidths(300, 24, 16, hasStart = false, hasEnd = true, startShare = 0.3f))
        assertEquals(260 to 0, sideWidths(300, 24, 16, hasStart = true, hasEnd = false, startShare = 0.3f))
    }

    @Test
    fun twoSidesSplitByShare() {
        // 300 - 24 - 2 * 16 = 244 to share.
        assertEquals(122 to 122, sideWidths(300, 24, 16, hasStart = true, hasEnd = true, startShare = 0.5f))
        assertEquals(73 to 171, sideWidths(300, 24, 16, hasStart = true, hasEnd = true, startShare = 0.3f))
    }

    @Test
    fun tooNarrowGivesNothing() {
        assertEquals(0 to 0, sideWidths(30, 24, 16, hasStart = true, hasEnd = true, startShare = 0.5f))
    }

    @Test
    fun revealDelayIsCapped() {
        assertEquals(0L, revealDelayMillis(0, 60L))
        assertEquals(180L, revealDelayMillis(3, 60L))
        assertEquals(revealDelayMillis(10, 60L), revealDelayMillis(500, 60L))
    }

    @Test
    fun connectorFillsOutgoingFirstAndEmptiesIncomingFirst() {
        assertEquals(0, segmentDelayMillis(incoming = false, reached = true, lineFillMillis = 500))
        assertEquals(250, segmentDelayMillis(incoming = true, reached = true, lineFillMillis = 500))
        assertEquals(0, segmentDelayMillis(incoming = true, reached = false, lineFillMillis = 500))
        assertEquals(250, segmentDelayMillis(incoming = false, reached = false, lineFillMillis = 500))
    }
}
