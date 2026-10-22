package com.nameisjayant.components.timeline

import androidx.compose.runtime.Immutable

/**
 * One step of a [ComposeTimeline], handed to its `indicator`, `content` and `oppositeContent` slots.
 *
 * @property index position in the timeline's items, 0-based.
 * @property item the item itself.
 * @property state where the step stands; see the timeline's `stepState`.
 * @property count number of steps in the timeline.
 * @property contentSide the side this step's content is on, [TimelineContentSide.Start] or
 * [TimelineContentSide.End] (never `Alternate`); useful for aligning text towards the line.
 * @property orientation which way the timeline runs.
 */
@Immutable
public class TimelineStep<out T>(
    public val index: Int,
    public val item: T,
    public val state: TimelineStepState,
    public val count: Int,
    public val contentSide: TimelineContentSide,
    public val orientation: TimelineOrientation,
) {

    public val isFirst: Boolean get() = index == 0
    public val isLast: Boolean get() = index == count - 1

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is TimelineStep<*> &&
                index == other.index &&
                item == other.item &&
                state == other.state &&
                count == other.count &&
                contentSide == other.contentSide &&
                orientation == other.orientation
            )

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + item.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + count
        result = 31 * result + contentSide.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }

    override fun toString(): String =
        "TimelineStep(index=$index, item=$item, state=$state, count=$count, contentSide=$contentSide, orientation=$orientation)"
}
