package com.nameisjayant.components.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key as stepKey
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

/**
 * A run of steps joined by a line: order tracking, onboarding progress, a history of events. Each step has
 * an indicator on the line, content beside it and, optionally, opposite content on the other side (a time,
 * say). The line is drawn as reached up to the furthest step that has been reached.
 *
 * ```
 * ComposeTimeline(
 *     items = orderSteps,
 *     stepState = { index, _ -> TimelineStepState.forProgress(index, currentStep) },
 *     oppositeContent = { step -> Text(step.item.time) },
 * ) { step ->
 *     Text(step.item.title)
 * }
 * ```
 *
 * This lays out every step. For a long, scrolling timeline use [timelineItems] inside a `LazyColumn`.
 *
 * @param items one per step.
 * @param stepState where each step stands; see [TimelineStepState.forProgress]. Every step is `Completed` by default.
 * @param key a stable key per item, so steps keep their animations when items move.
 * @param orientation [TimelineOrientation.Vertical] (top to bottom) or `Horizontal` (start to end).
 * @param contentSide which side of the line the content goes on, or `Alternate` to switch every step.
 * @param indicatorAlignment indicator level with the start of the content, or centered on it.
 * @param indicatorSize size of the default indicators, and the width of the track the line runs along.
 * Bigger custom indicators are centered on the track.
 * @param itemSpacing gap between steps; the line runs through it.
 * @param contentSpacing gap between the line and the content on either side.
 * @param lineGap space left between the line and each indicator.
 * @param itemWidth width of each step in a horizontal timeline. [Dp.Unspecified] shares the width equally,
 * which needs a bounded width; give a size to put the timeline in a horizontally scrolling container.
 * @param oppositeContentFraction share of the space beside the line that [oppositeContent] gets. Alternating
 * steps always split it equally.
 * @param lineStyle the line up to the furthest reached step.
 * @param pendingLineStyle the line after it, e.g. [TimelineLineStyle.Dashed].
 * @param colors see [ComposeTimelineDefaults.colors].
 * @param animation reveal, line fill, state blend and pulse; [TimelineAnimation.None] turns them off.
 * @param indicator draws each step's indicator: [StatusIndicator] (the default), [NumberIndicator],
 * [IconIndicator] or your own.
 * @param oppositeContent drawn on the other side of the line from [content].
 * @param content drawn beside each step's indicator. [TimelineStep.contentSide] says which side it's on.
 */
@Composable
public fun <T> ComposeTimeline(
    items: List<T>,
    modifier: Modifier = Modifier,
    stepState: (index: Int, item: T) -> TimelineStepState = { _, _ -> TimelineStepState.Completed },
    key: ((item: T) -> Any)? = null,
    orientation: TimelineOrientation = TimelineOrientation.Vertical,
    contentSide: TimelineContentSide = TimelineContentSide.End,
    indicatorAlignment: TimelineIndicatorAlignment = TimelineIndicatorAlignment.Start,
    indicatorSize: Dp = ComposeTimelineDefaults.IndicatorSize,
    itemSpacing: Dp = ComposeTimelineDefaults.ItemSpacing,
    contentSpacing: Dp = ComposeTimelineDefaults.ContentSpacing,
    lineGap: Dp = 0.dp,
    itemWidth: Dp = Dp.Unspecified,
    oppositeContentFraction: Float = ComposeTimelineDefaults.OppositeContentFraction,
    lineStyle: TimelineLineStyle = TimelineLineStyle.Solid,
    pendingLineStyle: TimelineLineStyle = lineStyle,
    colors: ComposeTimelineColors = ComposeTimelineDefaults.colors(),
    animation: TimelineAnimation = TimelineAnimation.Default,
    indicator: @Composable TimelineIndicatorScope.(step: TimelineStep<T>) -> Unit = { StatusIndicator(it.state) },
    oppositeContent: (@Composable (step: TimelineStep<T>) -> Unit)? = null,
    content: @Composable (step: TimelineStep<T>) -> Unit,
) {
    requireTimelineArguments(indicatorSize, itemSpacing, contentSpacing, lineGap, oppositeContentFraction)
    require(!itemWidth.isSpecified || itemWidth > 0.dp) { "itemWidth must be positive, was $itemWidth" }

    val config = TimelineConfig(
        orientation = orientation,
        contentSide = contentSide,
        indicatorAlignment = indicatorAlignment,
        indicatorSize = indicatorSize,
        itemSpacing = itemSpacing,
        contentSpacing = contentSpacing,
        lineGap = lineGap,
        oppositeContentFraction = oppositeContentFraction,
        lineStyle = lineStyle,
        pendingLineStyle = pendingLineStyle,
        colors = colors,
        animation = animation,
    )
    val count = items.size
    val states = items.mapIndexed { index, item -> stepState(index, item) }

    // Steps on screen from the start fade in one after another; steps added later fade in straight away.
    val firstComposition = remember { FirstComposition() }
    SideEffect { firstComposition.done = true }
    // Previews and screenshot tests don't run effects, so steps would stay invisible.
    val reveal = animation.enabled && animation.revealItems && !LocalInspectionMode.current
    val staggered = !firstComposition.done

    @Composable
    fun Step(index: Int, item: T, stepModifier: Modifier) {
        TimelineItem(
            step = TimelineStep(index, item, states[index], count, contentSideFor(index, contentSide), orientation),
            previousState = states.getOrNull(index - 1),
            nextState = states.getOrNull(index + 1),
            config = config,
            reveal = reveal,
            revealDelayMillis = if (staggered) revealDelayMillis(index, animation.staggerMillis) else 0L,
            indicator = indicator,
            oppositeContent = oppositeContent,
            content = content,
            modifier = stepModifier,
        )
    }

    when (orientation) {
        TimelineOrientation.Vertical -> Column(modifier.semantics { collectionInfo = CollectionInfo(count, 1) }) {
            items.forEachIndexed { index, item ->
                stepKey(key?.invoke(item) ?: index) {
                    Step(index, item, Modifier.semantics { collectionItemInfo = CollectionItemInfo(index, 1, 0, 1) })
                }
            }
        }

        TimelineOrientation.Horizontal -> Row(
            modifier.semantics { collectionInfo = CollectionInfo(1, count) },
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            items.forEachIndexed { index, item ->
                stepKey(key?.invoke(item) ?: index) {
                    Step(
                        index = index,
                        item = item,
                        stepModifier = Modifier
                            // Lines the tracks up, however tall the content above them is.
                            .alignBy(TimelineTrackLine)
                            .then(if (itemWidth.isSpecified) Modifier.width(itemWidth) else Modifier.weight(1f))
                            .semantics { collectionItemInfo = CollectionItemInfo(0, 1, index, 1) },
                    )
                }
            }
        }
    }
}

/**
 * Adds a vertical timeline to a `LazyColumn`, one lazy item per step, for long or endless timelines.
 * It can sit among other items. Parameters work as in [ComposeTimeline]; steps don't fade in on first show.
 *
 * ```
 * LazyColumn(contentPadding = PaddingValues(16.dp)) {
 *     item { Header() }
 *     timelineItems(events, key = { it.id }) { step -> EventCard(step.item) }
 * }
 * ```
 *
 * @param key a stable key per item; on Android it must be storable in a `Bundle`.
 * @param contentType lets the list reuse compositions between steps of the same type.
 * @param colors `null` uses [ComposeTimelineDefaults.colors].
 */
public fun <T> LazyListScope.timelineItems(
    items: List<T>,
    stepState: (index: Int, item: T) -> TimelineStepState = { _, _ -> TimelineStepState.Completed },
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    contentSide: TimelineContentSide = TimelineContentSide.End,
    indicatorAlignment: TimelineIndicatorAlignment = TimelineIndicatorAlignment.Start,
    indicatorSize: Dp = ComposeTimelineDefaults.IndicatorSize,
    itemSpacing: Dp = ComposeTimelineDefaults.ItemSpacing,
    contentSpacing: Dp = ComposeTimelineDefaults.ContentSpacing,
    lineGap: Dp = 0.dp,
    oppositeContentFraction: Float = ComposeTimelineDefaults.OppositeContentFraction,
    lineStyle: TimelineLineStyle = TimelineLineStyle.Solid,
    pendingLineStyle: TimelineLineStyle = lineStyle,
    colors: ComposeTimelineColors? = null,
    animation: TimelineAnimation = TimelineAnimation.Default,
    indicator: @Composable TimelineIndicatorScope.(step: TimelineStep<T>) -> Unit = { StatusIndicator(it.state) },
    oppositeContent: (@Composable (step: TimelineStep<T>) -> Unit)? = null,
    content: @Composable (step: TimelineStep<T>) -> Unit,
) {
    requireTimelineArguments(indicatorSize, itemSpacing, contentSpacing, lineGap, oppositeContentFraction)
    val count = items.size

    itemsIndexed(
        items = items,
        key = key?.let { itemKey -> { _: Int, item: T -> itemKey(item) } },
        contentType = { _, item -> contentType(item) },
    ) { index, item ->
        val config = TimelineConfig(
            orientation = TimelineOrientation.Vertical,
            contentSide = contentSide,
            indicatorAlignment = indicatorAlignment,
            indicatorSize = indicatorSize,
            itemSpacing = itemSpacing,
            contentSpacing = contentSpacing,
            lineGap = lineGap,
            oppositeContentFraction = oppositeContentFraction,
            lineStyle = lineStyle,
            pendingLineStyle = pendingLineStyle,
            colors = colors ?: ComposeTimelineDefaults.colors(),
            animation = animation,
        )
        TimelineItem(
            step = TimelineStep(
                index = index,
                item = item,
                state = stepState(index, item),
                count = count,
                contentSide = contentSideFor(index, contentSide),
                orientation = TimelineOrientation.Vertical,
            ),
            previousState = if (index > 0) stepState(index - 1, items[index - 1]) else null,
            nextState = if (index < count - 1) stepState(index + 1, items[index + 1]) else null,
            config = config,
            reveal = false,
            revealDelayMillis = 0L,
            indicator = indicator,
            oppositeContent = oppositeContent,
            content = content,
        )
    }
}

private class FirstComposition {
    var done = false
}

private fun requireTimelineArguments(
    indicatorSize: Dp,
    itemSpacing: Dp,
    contentSpacing: Dp,
    lineGap: Dp,
    oppositeContentFraction: Float,
) {
    require(indicatorSize > 0.dp) { "indicatorSize must be positive, was $indicatorSize" }
    require(itemSpacing >= 0.dp) { "itemSpacing can't be negative, was $itemSpacing" }
    require(contentSpacing >= 0.dp) { "contentSpacing can't be negative, was $contentSpacing" }
    require(lineGap >= 0.dp) { "lineGap can't be negative, was $lineGap" }
    require(oppositeContentFraction in 0f..1f) {
        "oppositeContentFraction must be in 0..1, was $oppositeContentFraction"
    }
}
