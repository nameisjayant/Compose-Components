package com.nameisjayant.components.timeline

/**
 * Where a step of a [ComposeTimeline] stands. It picks the look of the default indicator and the color of
 * the line: the line leading into a step is drawn as "reached" once that step is anything but [Upcoming].
 */
public enum class TimelineStepState {
    /** Done. The default indicator is filled and shows a check mark. */
    Completed,

    /** In progress. The default indicator is a ring with a dot and a soft pulse. */
    Active,

    /** Not reached yet. The default indicator is an empty ring. */
    Upcoming,

    /** Reached but failed. The default indicator is filled with the error color and shows an exclamation mark. */
    Error;

    /** The timeline has got this far: anything but [Upcoming]. */
    public val isReached: Boolean get() = this != Upcoming

    public companion object {
        /**
         * State of step [index] when the timeline is at step [currentIndex]: earlier steps are [Completed],
         * the current one [Active] and later ones [Upcoming]. Pass `currentIndex = items.size` to complete them all.
         */
        public fun forProgress(index: Int, currentIndex: Int): TimelineStepState = when {
            index < currentIndex -> Completed
            index == currentIndex -> Active
            else -> Upcoming
        }
    }
}
