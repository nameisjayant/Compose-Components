package com.nameisjayant.components.timeline

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeTimeline] and its indicators. Each [TimelineStepState] has an indicator color, and
 * an "on" color for the check mark, number or icon drawn on a filled indicator. The line is drawn in
 * [completedLineColor] up to the furthest reached step and [upcomingLineColor] after it.
 *
 * Get the theme-based defaults from [ComposeTimelineDefaults.colors], a palette around one color from
 * [ComposeTimelineDefaults.accentColors], or tweak any of them with [copy].
 *
 * @property containerColor fill inside ring-shaped indicators (upcoming and active steps).
 * @property haloColor the pulse around the active step's indicator.
 */
@Immutable
public class ComposeTimelineColors(
    public val completedColor: Color,
    public val onCompletedColor: Color,
    public val activeColor: Color,
    public val onActiveColor: Color,
    public val upcomingColor: Color,
    public val errorColor: Color,
    public val onErrorColor: Color,
    public val containerColor: Color,
    public val completedLineColor: Color,
    public val upcomingLineColor: Color,
    public val haloColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        completedColor: Color = Color.Unspecified,
        onCompletedColor: Color = Color.Unspecified,
        activeColor: Color = Color.Unspecified,
        onActiveColor: Color = Color.Unspecified,
        upcomingColor: Color = Color.Unspecified,
        errorColor: Color = Color.Unspecified,
        onErrorColor: Color = Color.Unspecified,
        containerColor: Color = Color.Unspecified,
        completedLineColor: Color = Color.Unspecified,
        upcomingLineColor: Color = Color.Unspecified,
        haloColor: Color = Color.Unspecified,
    ): ComposeTimelineColors = ComposeTimelineColors(
        completedColor = completedColor.or(this.completedColor),
        onCompletedColor = onCompletedColor.or(this.onCompletedColor),
        activeColor = activeColor.or(this.activeColor),
        onActiveColor = onActiveColor.or(this.onActiveColor),
        upcomingColor = upcomingColor.or(this.upcomingColor),
        errorColor = errorColor.or(this.errorColor),
        onErrorColor = onErrorColor.or(this.onErrorColor),
        containerColor = containerColor.or(this.containerColor),
        completedLineColor = completedLineColor.or(this.completedLineColor),
        upcomingLineColor = upcomingLineColor.or(this.upcomingLineColor),
        haloColor = haloColor.or(this.haloColor),
    )

    internal fun indicator(state: TimelineStepState): Color = when (state) {
        TimelineStepState.Completed -> completedColor
        TimelineStepState.Active -> activeColor
        TimelineStepState.Upcoming -> upcomingColor
        TimelineStepState.Error -> errorColor
    }

    /** Color of what's drawn on the indicator; upcoming indicators are empty rings, so their marks use the ring color. */
    internal fun onIndicator(state: TimelineStepState): Color = when (state) {
        TimelineStepState.Completed -> onCompletedColor
        TimelineStepState.Active -> onActiveColor
        TimelineStepState.Upcoming -> upcomingColor
        TimelineStepState.Error -> onErrorColor
    }

    internal fun line(reached: Boolean): Color = if (reached) completedLineColor else upcomingLineColor

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        completedColor, onCompletedColor, activeColor, onActiveColor, upcomingColor, errorColor, onErrorColor,
        containerColor, completedLineColor, upcomingLineColor, haloColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeTimelineColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
