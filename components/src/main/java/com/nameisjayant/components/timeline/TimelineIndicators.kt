package com.nameisjayant.components.timeline

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Receiver of a [ComposeTimeline]'s `indicator` slot. It carries the timeline's [colors], [indicatorSize] and
 * [animation], so the ready-made indicators ([StatusIndicator], [NumberIndicator], [IconIndicator]) match
 * the rest of the timeline without passing them again.
 */
@Stable
public interface TimelineIndicatorScope {
    public val colors: ComposeTimelineColors
    public val indicatorSize: Dp
    public val animation: TimelineAnimation
}

internal class TimelineIndicatorScopeImpl(
    override val colors: ComposeTimelineColors,
    override val indicatorSize: Dp,
    override val animation: TimelineAnimation,
) : TimelineIndicatorScope

/**
 * The default indicator: an empty ring for [TimelineStepState.Upcoming], a ring with a pulsing dot for
 * `Active`, a filled circle with a check mark for `Completed` and with an exclamation mark for `Error`.
 * Changes of state blend smoothly. The marks are left out below 14dp.
 */
@Composable
public fun TimelineIndicatorScope.StatusIndicator(
    state: TimelineStepState,
    modifier: Modifier = Modifier,
    size: Dp = indicatorSize,
) {
    val colors = colors
    val animated = animation.enabled
    val floatSpec: AnimationSpec<Float> = if (animated) tween(350, easing = FastOutSlowInEasing) else snap()
    val markSpec: AnimationSpec<Float> = if (animated) tween(320, delayMillis = 120, easing = FastOutSlowInEasing) else snap()

    val color by animateColorAsState(colors.indicator(state), colorSpec(animated), label = "TimelineIndicator")
    val onColor by animateColorAsState(colors.onIndicator(state), colorSpec(animated), label = "TimelineOnIndicator")
    val fill by animateFloatAsState(if (state == TimelineStepState.Completed || state == TimelineStepState.Error) 1f else 0f, floatSpec, label = "TimelineFill")
    val dot by animateFloatAsState(if (state == TimelineStepState.Active) 1f else 0f, floatSpec, label = "TimelineDot")
    val check by animateFloatAsState(if (state == TimelineStepState.Completed) 1f else 0f, markSpec, label = "TimelineCheck")
    val exclamation by animateFloatAsState(if (state == TimelineStepState.Error) 1f else 0f, markSpec, label = "TimelineExclamation")
    val pulse = activePulse(state)
    val containerColor = colors.containerColor
    val haloColor = colors.haloColor

    Spacer(
        modifier
            .size(size)
            .drawWithCache {
                val radius = this.size.minDimension / 2f
                val ringWidth = max(1.5.dp.toPx(), radius * 0.17f)
                val markStroke = Stroke(
                    width = max(1.5.dp.toPx(), this.size.minDimension * 0.1f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )
                val showMarks = this.size.minDimension >= 14.dp.toPx()
                val w = this.size.width
                val h = this.size.height
                val checkPath = Path().apply {
                    moveTo(w * 0.29f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.66f)
                    lineTo(w * 0.72f, h * 0.37f)
                }
                val checkMeasure = PathMeasure().apply { setPath(checkPath, false) }
                val checkSegment = Path()

                onDrawBehind {
                    pulse?.let { drawHalo(haloColor, radius, it.value) }
                    if (containerColor.alpha > 0f) drawCircle(containerColor, radius)
                    drawCircle(color, radius - ringWidth / 2f, style = Stroke(ringWidth))
                    if (fill > 0f) drawCircle(color, radius * fill)
                    if (dot > 0f) drawCircle(color, radius * 0.42f * dot)
                    if (!showMarks) return@onDrawBehind
                    if (check > 0f) {
                        checkSegment.reset()
                        checkMeasure.getSegment(0f, checkMeasure.length * check, checkSegment, true)
                        drawPath(checkSegment, onColor, style = markStroke)
                    }
                    if (exclamation > 0f) {
                        val top = h * 0.27f
                        drawLine(
                            color = onColor,
                            start = Offset(w / 2f, top),
                            end = Offset(w / 2f, top + h * 0.29f * exclamation),
                            strokeWidth = markStroke.width,
                            cap = StrokeCap.Round,
                        )
                        drawCircle(onColor, markStroke.width * 0.62f * exclamation, Offset(w / 2f, h * 0.73f))
                    }
                }
            },
    )
}

/**
 * A circle with [text] in it, usually the step number (`(step.index + 1).toString()`). Reached steps are
 * filled with their state's color; upcoming ones are an outlined ring.
 */
@Composable
public fun TimelineIndicatorScope.NumberIndicator(
    text: String,
    state: TimelineStepState,
    modifier: Modifier = Modifier,
    size: Dp = indicatorSize,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    IndicatorBadge(state, modifier, size) {
        Text(text = text, style = textStyle, maxLines = 1)
    }
}

/**
 * A circle with an icon in it. Reached steps are filled with their state's color; upcoming ones are an
 * outlined ring. The icon is tinted to match.
 */
@Composable
public fun TimelineIndicatorScope.IconIndicator(
    painter: Painter,
    state: TimelineStepState,
    modifier: Modifier = Modifier,
    size: Dp = indicatorSize,
    iconSize: Dp = size * 0.58f,
    contentDescription: String? = null,
) {
    IndicatorBadge(state, modifier, size) {
        Icon(painter = painter, contentDescription = contentDescription, modifier = Modifier.size(iconSize))
    }
}

/** [IconIndicator] for an [ImageVector]. */
@Composable
public fun TimelineIndicatorScope.IconIndicator(
    imageVector: ImageVector,
    state: TimelineStepState,
    modifier: Modifier = Modifier,
    size: Dp = indicatorSize,
    iconSize: Dp = size * 0.58f,
    contentDescription: String? = null,
) {
    IconIndicator(rememberVectorPainter(imageVector), state, modifier, size, iconSize, contentDescription)
}

/** Ring that fills once the step is reached, with [content] drawn in the matching "on" color. */
@Composable
private fun TimelineIndicatorScope.IndicatorBadge(
    state: TimelineStepState,
    modifier: Modifier,
    size: Dp,
    content: @Composable () -> Unit,
) {
    val colors = colors
    val animated = animation.enabled
    val color by animateColorAsState(colors.indicator(state), colorSpec(animated), label = "TimelineBadge")
    val onColor by animateColorAsState(colors.onIndicator(state), colorSpec(animated), label = "TimelineOnBadge")
    val fill by animateFloatAsState(
        targetValue = if (state.isReached) 1f else 0f,
        animationSpec = if (animated) tween(350, easing = FastOutSlowInEasing) else snap(),
        label = "TimelineBadgeFill",
    )
    val pulse = activePulse(state)
    val containerColor = colors.containerColor
    val haloColor = colors.haloColor

    Box(
        modifier
            .size(size)
            .drawBehind {
                val radius = this.size.minDimension / 2f
                val ringWidth = max(1.5.dp.toPx(), radius * 0.12f)
                pulse?.let { drawHalo(haloColor, radius, it.value) }
                if (containerColor.alpha > 0f) drawCircle(containerColor, radius)
                drawCircle(color, radius - ringWidth / 2f, style = Stroke(ringWidth))
                if (fill > 0f) drawCircle(color, radius * fill)
            },
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides onColor, content = content)
    }
}

/** 0→1 repeating progress for the active step's halo, or null when it shouldn't pulse. */
@Composable
private fun TimelineIndicatorScope.activePulse(state: TimelineStepState): State<Float>? {
    if (state != TimelineStepState.Active || !animation.enabled || !animation.pulseActive) return null
    return rememberInfiniteTransition(label = "TimelinePulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearOutSlowInEasing)),
        label = "TimelinePulseProgress",
    )
}

private fun DrawScope.drawHalo(color: Color, radius: Float, progress: Float) {
    drawCircle(color = color, radius = radius * (1f + 0.75f * progress), alpha = 0.35f * (1f - progress))
}

private fun colorSpec(animated: Boolean): AnimationSpec<Color> = if (animated) tween(300) else snap()
