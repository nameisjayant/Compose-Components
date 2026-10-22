package com.nameisjayant.components.ratingbar

import androidx.compose.runtime.Immutable

/**
 * How [ComposeRatingBar] moves.
 *
 * When the rating grows, items fill one after another ([staggerMillis] apart), each with a soft
 * [waveScale] bump; the item the user tapped pops to [popScale] and sends out a fading halo. While
 * dragging, the item under the finger lifts to [dragScale] and the fill tracks the finger.
 *
 * Use [None] to switch every animation off.
 */
@Immutable
public class RatingBarAnimation(
    public val enabled: Boolean = true,
    public val staggerMillis: Long = 55L,
    public val waveScale: Float = 1.1f,
    public val popScale: Float = 1.28f,
    public val dragScale: Float = 1.18f,
    public val showHalo: Boolean = true,
) {

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is RatingBarAnimation &&
                enabled == other.enabled &&
                staggerMillis == other.staggerMillis &&
                waveScale == other.waveScale &&
                popScale == other.popScale &&
                dragScale == other.dragScale &&
                showHalo == other.showHalo
            )

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + staggerMillis.hashCode()
        result = 31 * result + waveScale.hashCode()
        result = 31 * result + popScale.hashCode()
        result = 31 * result + dragScale.hashCode()
        result = 31 * result + showHalo.hashCode()
        return result
    }

    public companion object {
        public val Default: RatingBarAnimation = RatingBarAnimation()

        /** Rating changes are applied instantly, with no scaling or halo. */
        public val None: RatingBarAnimation = RatingBarAnimation(enabled = false)
    }
}
