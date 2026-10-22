package com.nameisjayant.components.wheelpicker

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Number of rows the underlying list holds: the items themselves, or for a cyclic wheel as many whole
 * copies of them as fit in an Int, so the wheel can be spun for (practically) ever in both directions.
 */
internal fun wheelRawCount(count: Int, cyclic: Boolean): Int = when {
    count <= 0 -> 0
    !cyclic -> count
    else -> Int.MAX_VALUE - Int.MAX_VALUE % count
}

/** Row that shows item [index] when the wheel first appears; a cyclic wheel starts in the middle copy. */
internal fun wheelStartRawIndex(index: Int, count: Int, cyclic: Boolean): Int {
    if (count <= 0) return 0
    val item = index.coerceIn(0, count - 1)
    if (!cyclic) return item
    val middle = wheelRawCount(count, cyclic = true) / 2
    return middle - middle % count + item
}

/** The item a row shows. */
internal fun wheelLogicalIndex(raw: Int, count: Int): Int = if (count <= 0) 0 else raw.mod(count)

/**
 * Row to scroll to so the wheel shows item [index]: the item itself, or for a cyclic wheel its copy
 * closest to [currentRaw], so the wheel takes the short way round.
 */
internal fun wheelNearestRawIndex(currentRaw: Int, index: Int, count: Int, cyclic: Boolean): Int {
    if (count <= 0) return 0
    val item = index.coerceIn(0, count - 1)
    if (!cyclic) return item
    var delta = (item - currentRaw.mod(count)).mod(count)
    if (delta > count / 2) delta -= count
    return (currentRaw + delta).coerceIn(0, wheelRawCount(count, cyclic = true) - 1)
}

/** Row under the selection line, given the list's first visible row and how far it has scrolled off. */
internal fun wheelCenterRawIndex(firstIndex: Int, firstOffset: Int, itemPx: Int): Int =
    if (itemPx <= 0) firstIndex else firstIndex + if (firstOffset * 2 >= itemPx) 1 else 0

/**
 * Closest item to [index] that [isEnabled] accepts, looking one step after, then one before, and so on
 * (wrapping round on a cyclic wheel); null when no item is enabled.
 */
internal fun wheelNearestEnabledIndex(index: Int, count: Int, cyclic: Boolean, isEnabled: (Int) -> Boolean): Int? {
    if (count <= 0) return null
    val start = index.coerceIn(0, count - 1)
    if (isEnabled(start)) return start
    for (step in 1 until count) {
        val after = start + step
        val before = start - step
        if (cyclic) {
            if (isEnabled(after.mod(count))) return after.mod(count)
            if (isEnabled(before.mod(count))) return before.mod(count)
        } else {
            if (after >= count && before < 0) return null
            if (after < count && isEnabled(after)) return after
            if (before >= 0 && isEnabled(before)) return before
        }
    }
    return null
}

/*
 * A curved wheel lays the rows on a cylinder whose radius is half the wheel's height, so a row that is
 * `distance` rows from the center sits at an angle of distance / radius and, seen from the front,
 * radius * sin(angle) rows from the center.
 */

/** Radius of the wheel's cylinder, in rows. */
internal fun wheelRadius(visibleItemCount: Int): Float = visibleItemCount / 2f

/** Angle of a row on the cylinder, in radians. */
internal fun wheelAngle(distance: Float, visibleItemCount: Int): Float = distance / wheelRadius(visibleItemCount)

/** Whether a row has turned to the back of the cylinder. */
internal fun wheelRowHidden(distance: Float, visibleItemCount: Int): Boolean =
    abs(wheelAngle(distance, visibleItemCount)) >= PI.toFloat() / 2f

/** How far, in rows, a curved row moves from its flat position (towards the center). */
internal fun wheelCurveShift(distance: Float, visibleItemCount: Int): Float =
    wheelRadius(visibleItemCount) * sin(wheelAngle(distance, visibleItemCount)) - distance

/** 0 at the center, 1 at the edge of the wheel; drives the fade and shrink of rows away from the center. */
internal fun wheelEdgeFraction(distance: Float, visibleItemCount: Int): Float =
    (abs(distance) / wheelRadius(visibleItemCount)).coerceIn(0f, 1f)
