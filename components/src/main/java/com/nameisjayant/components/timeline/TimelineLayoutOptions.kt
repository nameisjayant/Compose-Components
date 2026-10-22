package com.nameisjayant.components.timeline

/** Which way a [ComposeTimeline] runs. */
public enum class TimelineOrientation {
    /** Steps run top to bottom; content sits beside the line. */
    Vertical,

    /** Steps run start to end; content sits above or below the line. */
    Horizontal,
}

/**
 * Which side of the line each step's content goes on. Optional opposite content goes on the other side.
 *
 * In a vertical timeline [Start] and [End] are the start and end edges (left and right in left-to-right
 * layouts); in a horizontal one they are above and below the line.
 */
public enum class TimelineContentSide {
    /** Content before the line: on its start side, or above it. */
    Start,

    /** Content after the line: on its end side, or below it. The default. */
    End,

    /** Content switches sides from step to step, starting at [End]; the line runs down the middle. */
    Alternate,
}

/** Where the indicator sits along each step. */
public enum class TimelineIndicatorAlignment {
    /** Level with the start of the content: its top in a vertical timeline, its start edge in a horizontal one. */
    Start,

    /** In the middle of the content. */
    Center,
}
