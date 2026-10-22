package com.nameisjayant.components.timeline

import kotlin.math.roundToInt

/** The side step [index]'s content goes on; `Alternate` starts at the end and switches every step. */
internal fun contentSideFor(index: Int, side: TimelineContentSide): TimelineContentSide = when (side) {
    TimelineContentSide.Alternate -> if (index % 2 == 0) TimelineContentSide.End else TimelineContentSide.Start
    else -> side
}

/** The line from step [from] into the next step [to] counts as reached once both have been reached. */
internal fun connectorReached(from: TimelineStepState, to: TimelineStepState): Boolean =
    from.isReached && to.isReached

/**
 * Widths of the start and end columns beside a vertical track, from the [available] width.
 * A missing side gets nothing and no gap; when both are there the start side gets [startShare] of the room.
 */
internal fun sideWidths(
    available: Int,
    track: Int,
    gap: Int,
    hasStart: Boolean,
    hasEnd: Boolean,
    startShare: Float,
): Pair<Int, Int> {
    val gaps = (if (hasStart) gap else 0) + (if (hasEnd) gap else 0)
    val free = (available - track - gaps).coerceAtLeast(0)
    return when {
        hasStart && hasEnd -> {
            val start = (free * startShare.coerceIn(0f, 1f)).roundToInt()
            start to free - start
        }
        hasStart -> free to 0
        hasEnd -> 0 to free
        else -> 0 to 0
    }
}

/** How long step [index] waits before fading in when the timeline first shows; capped so long lists don't drag. */
internal fun revealDelayMillis(index: Int, staggerMillis: Long): Long = index.coerceIn(0, MaxStaggeredSteps) * staggerMillis

private const val MaxStaggeredSteps = 10

/**
 * Delay before one half of a connector animates. A connector is the outgoing segment of one step plus the
 * incoming segment of the next: when it fills, the outgoing half goes first; when it empties, the incoming
 * half does, so the line always moves away from or back to the earlier step.
 */
internal fun segmentDelayMillis(incoming: Boolean, reached: Boolean, lineFillMillis: Int): Int =
    if (incoming == reached) lineFillMillis / 2 else 0
