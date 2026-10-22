package com.nameisjayant.components.timeline

import androidx.compose.runtime.Immutable

/**
 * How [ComposeTimeline] moves.
 *
 * When it first shows, steps fade and rise in one after another ([staggerMillis] apart); steps added later
 * fade in on their own. When a step is reached, the line grows into it over [lineFillMillis] (and draws
 * back when it's un-reached), and the default indicators blend between states. The [TimelineStepState.Active]
 * indicator pulses.
 *
 * Use [None] to switch every animation off.
 *
 * @property revealItems fade steps in when they first show. [timelineItems] in a lazy list ignores this.
 * @property pulseActive let the default indicator of the active step send out a soft, repeating halo.
 */
@Immutable
public class TimelineAnimation(
    public val enabled: Boolean = true,
    public val revealItems: Boolean = true,
    public val staggerMillis: Long = 60L,
    public val lineFillMillis: Int = 500,
    public val pulseActive: Boolean = true,
) {

    init {
        require(staggerMillis >= 0L) { "staggerMillis can't be negative, was $staggerMillis" }
        require(lineFillMillis >= 0) { "lineFillMillis can't be negative, was $lineFillMillis" }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is TimelineAnimation &&
                enabled == other.enabled &&
                revealItems == other.revealItems &&
                staggerMillis == other.staggerMillis &&
                lineFillMillis == other.lineFillMillis &&
                pulseActive == other.pulseActive
            )

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + revealItems.hashCode()
        result = 31 * result + staggerMillis.hashCode()
        result = 31 * result + lineFillMillis
        result = 31 * result + pulseActive.hashCode()
        return result
    }

    public companion object {
        public val Default: TimelineAnimation = TimelineAnimation()

        /** Everything changes instantly, and nothing pulses. */
        public val None: TimelineAnimation = TimelineAnimation(enabled = false)
    }
}
