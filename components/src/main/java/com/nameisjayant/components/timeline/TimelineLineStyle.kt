package com.nameisjayant.components.timeline

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How the line between the indicators of a [ComposeTimeline] is drawn.
 *
 * A [gapLength] above zero breaks the line into dashes of [dashLength]. With a round [cap] each dash grows
 * by half the [width] at both ends, so a zero [dashLength] gives dots.
 *
 * Use [Solid], [Dashed] or [Dotted], or build your own.
 */
@Immutable
public class TimelineLineStyle(
    public val width: Dp = 2.dp,
    public val dashLength: Dp = 0.dp,
    public val gapLength: Dp = 0.dp,
    public val cap: StrokeCap = StrokeCap.Butt,
) {

    init {
        require(width > 0.dp) { "width must be positive, was $width" }
        require(dashLength >= 0.dp) { "dashLength can't be negative, was $dashLength" }
        require(gapLength >= 0.dp) { "gapLength can't be negative, was $gapLength" }
    }

    internal fun pathEffect(density: Density): PathEffect? {
        if (gapLength <= 0.dp) return null
        return with(density) {
            // A zero-length "on" interval isn't drawn on every device; a hair's width still gets its round caps.
            PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx().coerceAtLeast(0.01f), gapLength.toPx()))
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is TimelineLineStyle &&
                width == other.width &&
                dashLength == other.dashLength &&
                gapLength == other.gapLength &&
                cap == other.cap
            )

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + dashLength.hashCode()
        result = 31 * result + gapLength.hashCode()
        result = 31 * result + cap.hashCode()
        return result
    }

    override fun toString(): String =
        "TimelineLineStyle(width=$width, dashLength=$dashLength, gapLength=$gapLength, cap=$cap)"

    public companion object {
        /** An unbroken 2dp line. */
        public val Solid: TimelineLineStyle = TimelineLineStyle()

        /** 6dp dashes with 4dp gaps. */
        public val Dashed: TimelineLineStyle = TimelineLineStyle(dashLength = 6.dp, gapLength = 4.dp)

        /** Round 3dp dots. */
        public val Dotted: TimelineLineStyle = TimelineLineStyle(width = 3.dp, gapLength = 7.dp, cap = StrokeCap.Round)
    }
}
