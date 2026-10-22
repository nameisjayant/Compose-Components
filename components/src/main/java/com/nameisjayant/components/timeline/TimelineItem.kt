package com.nameisjayant.components.timeline

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.R
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

/** Middle of the track in a horizontal step, so a `Row` can line the tracks of all steps up. */
internal val TimelineTrackLine = HorizontalAlignmentLine(::min)

/** Everything about a timeline that's the same for each of its steps. */
internal data class TimelineConfig(
    val orientation: TimelineOrientation,
    val contentSide: TimelineContentSide,
    val indicatorAlignment: TimelineIndicatorAlignment,
    val indicatorSize: Dp,
    val itemSpacing: Dp,
    val contentSpacing: Dp,
    val lineGap: Dp,
    val oppositeContentFraction: Float,
    val lineStyle: TimelineLineStyle,
    val pendingLineStyle: TimelineLineStyle,
    val colors: ComposeTimelineColors,
    val animation: TimelineAnimation,
)

private enum class TimelineSlot { LineIn, LineOut, Indicator, Content, Opposite }

/**
 * One step: its indicator, the line into it and out of it, its content and optional opposite content.
 *
 * Vertical steps include the gap to the next step, so the line is unbroken even when a lazy list only
 * composes some of them. Horizontal steps draw their outgoing line past their end, across the gap the
 * row leaves between steps.
 */
@Composable
internal fun <T> TimelineItem(
    step: TimelineStep<T>,
    previousState: TimelineStepState?,
    nextState: TimelineStepState?,
    config: TimelineConfig,
    reveal: Boolean,
    revealDelayMillis: Long,
    indicator: @Composable TimelineIndicatorScope.(TimelineStep<T>) -> Unit,
    oppositeContent: (@Composable (TimelineStep<T>) -> Unit)?,
    content: @Composable (TimelineStep<T>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animation = config.animation
    val inReached = previousState != null && connectorReached(previousState, step.state)
    val outReached = nextState != null && connectorReached(step.state, nextState)
    val inProgress = remember { Animatable(if (inReached) 1f else 0f) }
    val outProgress = remember { Animatable(if (outReached) 1f else 0f) }
    LaunchedEffect(inReached, animation) { inProgress.animateSegment(inReached, incoming = true, animation) }
    LaunchedEffect(outReached, animation) { outProgress.animateSegment(outReached, incoming = false, animation) }

    // Decided once: a step that was already on screen never fades in again.
    val revealProgress = remember { if (reveal) Animatable(0f) else null }
    if (revealProgress != null) {
        LaunchedEffect(revealProgress) {
            delay(revealDelayMillis)
            revealProgress.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        }
    }
    val revealModifier = if (revealProgress != null) {
        Modifier.graphicsLayer {
            val p = revealProgress.value
            alpha = p
            translationY = (1f - p) * 16.dp.toPx()
        }
    } else {
        Modifier
    }

    val scope = remember(config.colors, config.indicatorSize, animation) {
        TimelineIndicatorScopeImpl(config.colors, config.indicatorSize, animation)
    }
    val stateText = stringResource(
        when (step.state) {
            TimelineStepState.Completed -> R.string.cctl_state_completed
            TimelineStepState.Active -> R.string.cctl_state_active
            TimelineStepState.Upcoming -> R.string.cctl_state_upcoming
            TimelineStepState.Error -> R.string.cctl_state_error
        },
    )
    val vertical = config.orientation == TimelineOrientation.Vertical

    Layout(
        content = {
            LineSegment({ inProgress.value }, vertical, config, Modifier.layoutId(TimelineSlot.LineIn))
            LineSegment({ outProgress.value }, vertical, config, Modifier.layoutId(TimelineSlot.LineOut))
            Box(Modifier.layoutId(TimelineSlot.Indicator), contentAlignment = Alignment.Center) { scope.indicator(step) }
            Box(Modifier.layoutId(TimelineSlot.Content)) { content(step) }
            if (oppositeContent != null) {
                Box(Modifier.layoutId(TimelineSlot.Opposite)) { oppositeContent(step) }
            }
        },
        modifier = modifier
            .then(revealModifier)
            .semantics(mergeDescendants = true) { stateDescription = stateText },
    ) { measurables, constraints ->
        if (vertical) {
            measureVertical(measurables, constraints, step, config)
        } else {
            measureHorizontal(measurables, constraints, step, config)
        }
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animateSegment(
    reached: Boolean,
    incoming: Boolean,
    animation: TimelineAnimation,
) {
    val target = if (reached) 1f else 0f
    if (value == target) return
    if (!animation.enabled || animation.lineFillMillis == 0) {
        snapTo(target)
        return
    }
    val delayMillis = segmentDelayMillis(incoming, reached, animation.lineFillMillis)
    // The first half speeds up and the second slows down, so the two read as one movement.
    val easing = if (delayMillis > 0) LinearOutSlowInEasing else FastOutLinearInEasing
    animateTo(target, tween(animation.lineFillMillis / 2, delayMillis, easing))
}

/** Track runs down the start or middle; content and opposite content sit in columns beside it. */
private fun MeasureScope.measureVertical(
    measurables: List<Measurable>,
    constraints: Constraints,
    step: TimelineStep<*>,
    config: TimelineConfig,
): MeasureResult {
    val track = config.indicatorSize.roundToPx()
    val gap = config.contentSpacing.roundToPx()
    val spacing = if (step.isLast) 0 else config.itemSpacing.roundToPx()
    val lineGap = config.lineGap.roundToPx()
    val lineWidth = lineWidthPx(config)

    val indicator = measurables.slot(TimelineSlot.Indicator).measure(Constraints())
    val contentOnEnd = step.contentSide == TimelineContentSide.End
    val alternate = config.contentSide == TimelineContentSide.Alternate
    val contentM = measurables.slot(TimelineSlot.Content)
    val oppositeM = measurables.slotOrNull(TimelineSlot.Opposite)
    // Alternating steps keep both columns so the track stays in the middle.
    val twoSided = oppositeM != null || alternate
    val startM = if (contentOnEnd) oppositeM else contentM
    val endM = if (contentOnEnd) contentM else oppositeM
    val hasStart = startM != null || twoSided
    val hasEnd = endM != null || twoSided

    val bounded = constraints.hasBoundedWidth
    val (startMax, endMax) = if (bounded) {
        val startShare = when {
            alternate -> 0.5f
            contentOnEnd -> config.oppositeContentFraction
            else -> 1f - config.oppositeContentFraction
        }
        sideWidths(constraints.maxWidth, track, gap, hasStart, hasEnd, startShare)
    } else {
        Constraints.Infinity to Constraints.Infinity
    }
    val startP = startM?.measure(Constraints(maxWidth = startMax))
    val endP = endM?.measure(Constraints(maxWidth = endMax))

    val startColumn = if (bounded) startMax else startP?.width ?: 0
    val endColumn = if (bounded) endMax else endP?.width ?: 0
    val trackX = startColumn + if (hasStart) gap else 0
    val width = if (bounded) constraints.maxWidth else constraints.constrainWidth(trackX + track + if (hasEnd) gap + endColumn else 0)

    val body = maxOf(indicator.height, startP?.height ?: 0, endP?.height ?: 0)
    val height = constraints.constrainHeight(body + spacing)
    val centered = config.indicatorAlignment == TimelineIndicatorAlignment.Center
    fun crossY(h: Int) = if (centered) (body - h) / 2 else 0

    val indicatorY = crossY(indicator.height)
    val inLength = if (step.isFirst) 0 else (indicatorY - lineGap).coerceAtLeast(0)
    val outStart = indicatorY + indicator.height + lineGap
    val outLength = if (step.isLast) 0 else (height - outStart).coerceAtLeast(0)
    val lineIn = measurables.slot(TimelineSlot.LineIn).measure(Constraints.fixed(lineWidth, inLength))
    val lineOut = measurables.slot(TimelineSlot.LineOut).measure(Constraints.fixed(lineWidth, outLength))
    val lineX = trackX + (track - lineWidth) / 2

    return layout(width, height) {
        lineIn.placeRelative(lineX, 0)
        lineOut.placeRelative(lineX, outStart)
        // Both columns hug the track.
        startP?.placeRelative(startColumn - startP.width, crossY(startP.height))
        endP?.placeRelative(trackX + track + gap, crossY(endP.height))
        indicator.placeRelative(trackX + (track - indicator.width) / 2, indicatorY)
    }
}

/** Track runs across; content and opposite content sit above and below it. */
private fun MeasureScope.measureHorizontal(
    measurables: List<Measurable>,
    constraints: Constraints,
    step: TimelineStep<*>,
    config: TimelineConfig,
): MeasureResult {
    val track = config.indicatorSize.roundToPx()
    val gap = config.contentSpacing.roundToPx()
    val spacing = if (step.isLast) 0 else config.itemSpacing.roundToPx()
    val lineGap = config.lineGap.roundToPx()
    val lineWidth = lineWidthPx(config)

    val indicator = measurables.slot(TimelineSlot.Indicator).measure(Constraints())
    val contentOnEnd = step.contentSide == TimelineContentSide.End
    val bounded = constraints.hasBoundedWidth
    val childConstraints = Constraints(maxWidth = if (bounded) constraints.maxWidth else Constraints.Infinity)
    val contentP = measurables.slot(TimelineSlot.Content).measure(childConstraints)
    val oppositeP = measurables.slotOrNull(TimelineSlot.Opposite)?.measure(childConstraints)
    val twoSided = oppositeP != null || config.contentSide == TimelineContentSide.Alternate
    val topP = if (contentOnEnd) oppositeP else contentP
    val bottomP = if (contentOnEnd) contentP else oppositeP
    val hasTop = topP != null || twoSided
    val hasBottom = bottomP != null || twoSided

    val width = if (bounded) {
        constraints.maxWidth
    } else {
        constraints.constrainWidth(maxOf(track, indicator.width, contentP.width, oppositeP?.width ?: 0))
    }
    val trackY = if (hasTop) (topP?.height ?: 0) + gap else 0
    val height = constraints.constrainHeight(trackY + track + if (hasBottom) gap + (bottomP?.height ?: 0) else 0)
    val centered = config.indicatorAlignment == TimelineIndicatorAlignment.Center
    fun crossX(w: Int) = if (centered) (width - w) / 2 else 0

    val indicatorX = if (centered) (width - indicator.width) / 2 else (track - indicator.width) / 2
    val inLength = if (step.isFirst) 0 else (indicatorX - lineGap).coerceAtLeast(0)
    val outStart = indicatorX + indicator.width + lineGap
    // Runs on past this step's end, through the gap the row leaves before the next step.
    val outLength = if (step.isLast) 0 else (width - outStart + spacing).coerceAtLeast(0)
    val lineIn = measurables.slot(TimelineSlot.LineIn).measure(Constraints.fixed(inLength, lineWidth))
    val lineOut = measurables.slot(TimelineSlot.LineOut).measure(Constraints.fixed(outLength, lineWidth))
    val lineY = trackY + (track - lineWidth) / 2

    return layout(width, height, mapOf(TimelineTrackLine to trackY + track / 2)) {
        lineIn.placeRelative(0, lineY)
        lineOut.placeRelative(outStart, lineY)
        topP?.placeRelative(crossX(topP.width), trackY - gap - topP.height)
        bottomP?.placeRelative(crossX(bottomP.width), trackY + track + gap)
        indicator.placeRelative(indicatorX, trackY + (track - indicator.height) / 2)
    }
}

private fun MeasureScope.lineWidthPx(config: TimelineConfig): Int =
    max(config.lineStyle.width.roundToPx(), config.pendingLineStyle.width.roundToPx()).coerceAtLeast(1)

private fun List<Measurable>.slot(slot: TimelineSlot): Measurable = first { it.layoutId == slot }

private fun List<Measurable>.slotOrNull(slot: TimelineSlot): Measurable? = firstOrNull { it.layoutId == slot }

/**
 * Half of a connector. It fills from its start (the top, or the start edge) as [progress] goes 0→1: in the
 * reached line style up to the progress and the pending style after it.
 */
@Composable
private fun LineSegment(progress: () -> Float, vertical: Boolean, config: TimelineConfig, modifier: Modifier) {
    val reachedColor = config.colors.line(reached = true)
    val pendingColor = config.colors.line(reached = false)
    val reachedStyle = config.lineStyle
    val pendingStyle = config.pendingLineStyle
    Spacer(
        modifier.drawWithCache {
            val reachedEffect = reachedStyle.pathEffect(this)
            val pendingEffect = pendingStyle.pathEffect(this)
            val reachedWidth = reachedStyle.width.toPx()
            val pendingWidth = pendingStyle.width.toPx()
            onDrawBehind {
                val length = if (vertical) size.height else size.width
                if (length <= 0f) return@onDrawBehind
                // A horizontal line in a right-to-left layout fills from the right.
                val reversed = !vertical && layoutDirection == LayoutDirection.Rtl
                val split = length * progress().coerceIn(0f, 1f)

                fun DrawScope.part(from: Float, to: Float, color: Color, style: TimelineLineStyle, width: Float, effect: PathEffect?) {
                    if (to <= from || color.alpha == 0f) return
                    val clipStart = if (reversed) length - to else from
                    val clipEnd = if (reversed) length - from else to
                    // Always draw the whole length from the same end so dashes don't slide as the split moves.
                    val origin = if (reversed) length else 0f
                    val end = length - origin
                    if (vertical) {
                        clipRect(top = clipStart, bottom = clipEnd) {
                            drawLine(color, Offset(size.width / 2f, origin), Offset(size.width / 2f, end), width, style.cap, effect)
                        }
                    } else {
                        clipRect(left = clipStart, right = clipEnd) {
                            drawLine(color, Offset(origin, size.height / 2f), Offset(end, size.height / 2f), width, style.cap, effect)
                        }
                    }
                }

                part(0f, split, reachedColor, reachedStyle, reachedWidth, reachedEffect)
                part(split, length, pendingColor, pendingStyle, pendingWidth, pendingEffect)
            }
        },
    )
}
