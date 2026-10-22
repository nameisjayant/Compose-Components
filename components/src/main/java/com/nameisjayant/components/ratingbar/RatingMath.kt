package com.nameisjayant.components.ratingbar

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Raw (unsnapped) rating for a horizontal position measured from the start edge.
 * A position in the gap after an item counts as that item being full.
 */
internal fun ratingAtPosition(x: Float, itemSizePx: Float, spacingPx: Float, maxRating: Int): Float {
    if (x <= 0f || itemSizePx <= 0f) return 0f
    val pitch = itemSizePx + spacingPx
    val index = (x / pitch).toInt()
    if (index >= maxRating) return maxRating.toFloat()
    val within = ((x - index * pitch) / itemSizePx).coerceIn(0f, 1f)
    return index + within
}

/** Index of the item under [x], clamped to the bar. */
internal fun itemAtPosition(x: Float, itemSizePx: Float, spacingPx: Float, maxRating: Int): Int {
    val pitch = itemSizePx + spacingPx
    if (pitch <= 0f) return 0
    return (x / pitch).toInt().coerceIn(0, maxRating - 1)
}

/**
 * Snaps [raw] to a multiple of [stepSize] (0 = no snapping) and clamps it to [minRating]..[maxRating].
 *
 * Touch input rounds up ([roundUp]) so touching anywhere on a star selects it; accessibility input rounds
 * to the nearest step.
 */
internal fun snapRating(raw: Float, stepSize: Float, minRating: Float, maxRating: Int, roundUp: Boolean): Float {
    val stepped = if (stepSize <= 0f) {
        raw
    } else {
        val steps = raw / stepSize
        // The small epsilon keeps 2.0000001 from rounding up to the next step.
        (if (roundUp) ceil(steps - 1e-3f) else steps.roundToInt().toFloat()) * stepSize
    }
    val tidy = (stepped * 1000f).roundToInt() / 1000f
    return tidy.coerceIn(minRating, maxRating.toFloat())
}

/** How full item [index] is for [rating]: 0 = empty, 1 = full. */
internal fun fractionOf(rating: Float, index: Int): Float = (rating - index).coerceIn(0f, 1f)

/** "4" or "4.5", localised. */
internal fun formatRating(rating: Float, locale: Locale): String =
    NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 1 }.format(rating)
