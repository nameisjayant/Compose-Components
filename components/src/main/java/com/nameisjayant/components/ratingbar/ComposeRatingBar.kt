package com.nameisjayant.components.ratingbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.R
import com.nameisjayant.components.datepicker.currentLocale
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * A row of stars (or any [Shape] / [Painter]) for showing and picking a rating. Tap an item to rate,
 * or drag across the bar to sweep the rating along with your finger.
 *
 * The bar is stateless: hold the rating yourself and update it in [onRatingChange].
 *
 * ```
 * var rating by rememberSaveable { mutableFloatStateOf(3f) }
 * ComposeRatingBar(rating = rating, onRatingChange = { rating = it }, stepSize = 0.5f)
 * ```
 *
 * @param rating current rating, from 0 to [maxRating]. Fractions show as partly filled items.
 * @param onRatingChange called with the new rating while the user taps or drags. `null` makes the bar
 * read-only (it still animates when [rating] changes).
 * @param maxRating number of items.
 * @param stepSize what the user's rating snaps to: 1 = whole items, 0.5 = halves, 0 = any value.
 * @param minRating the lowest rating the user can pick, e.g. 1 to require at least one star.
 * @param enabled when false the bar ignores input and uses the disabled colors.
 * @param allowClear tapping the current rating again resets it to [minRating].
 * @param itemSize width and height of each item.
 * @param itemSpacing gap between items.
 * @param shape outline of each item; see [ComposeRatingBarDefaults.StarShape], `SharpStarShape`,
 * `HeartShape`, `starShape(...)`, or any Compose shape such as `CircleShape`.
 * @param painter draw this (tinted with the bar's colors) instead of [shape], e.g. a vector icon.
 * @param borderWidth stroke drawn around each [shape]; 0 = none. Use with [ComposeRatingBarDefaults.outlinedColors]
 * for outline-only empty items.
 * @param colors see [ComposeRatingBarDefaults.colors].
 * @param animation fill, pop, drag and halo animations; [RatingBarAnimation.None] turns them off.
 * @param hapticFeedbackEnabled tick as the rating changes under the finger.
 * @param onRatingChangeFinished called when a tap or drag ends, e.g. to save the rating.
 */
@Composable
public fun ComposeRatingBar(
    rating: Float,
    onRatingChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    stepSize: Float = 1f,
    minRating: Float = 0f,
    enabled: Boolean = true,
    allowClear: Boolean = true,
    itemSize: Dp = ComposeRatingBarDefaults.ItemSize,
    itemSpacing: Dp = ComposeRatingBarDefaults.ItemSpacing,
    shape: Shape = ComposeRatingBarDefaults.StarShape,
    painter: Painter? = null,
    borderWidth: Dp = 0.dp,
    colors: ComposeRatingBarColors = ComposeRatingBarDefaults.colors(),
    animation: RatingBarAnimation = RatingBarAnimation.Default,
    hapticFeedbackEnabled: Boolean = true,
    onRatingChangeFinished: (() -> Unit)? = null,
) {
    require(maxRating >= 1) { "maxRating must be at least 1, was $maxRating" }
    require(stepSize >= 0f) { "stepSize can't be negative, was $stepSize" }
    require(minRating in 0f..maxRating.toFloat()) { "minRating must be in 0..$maxRating, was $minRating" }

    val displayRating = rating.coerceIn(0f, maxRating.toFloat())
    val interactive = enabled && onRatingChange != null

    val scope = rememberCoroutineScope()
    val animator = remember(maxRating) { RatingBarAnimator(maxRating, displayRating, scope) }
    LaunchedEffect(animator, displayRating) { animator.animateTo(displayRating, animation) }

    val colorSpec = tween<Color>(durationMillis = 300)
    val activeColor by animateColorAsState(colors.active(enabled), colorSpec, label = "RatingActive")
    val inactiveColor by animateColorAsState(colors.inactive(enabled), colorSpec, label = "RatingInactive")
    val activeBorderColor by animateColorAsState(colors.activeBorder(enabled), colorSpec, label = "RatingActiveBorder")
    val inactiveBorderColor by animateColorAsState(colors.inactiveBorder(enabled), colorSpec, label = "RatingInactiveBorder")
    val haloColor = colors.haloColor

    val density = LocalDensity.current
    val itemPx = with(density) { itemSize.toPx() }
    val spacingPx = with(density) { itemSpacing.toPx() }
    val layoutDirection = LocalLayoutDirection.current
    val rtl = layoutDirection == LayoutDirection.Rtl

    val currentRating by rememberUpdatedState(displayRating)
    val currentOnChange by rememberUpdatedState(onRatingChange)
    val currentOnFinished by rememberUpdatedState(onRatingChangeFinished)
    val currentAnimation by rememberUpdatedState(animation)
    val haptics = LocalHapticFeedback.current

    // Position from the start edge, so RTL fills from the right.
    fun startX(x: Float, width: Int): Float = if (rtl) width - x else x

    val gestures = if (interactive) {
        Modifier
            .pointerInput(maxRating, stepSize, minRating, itemPx, spacingPx, allowClear, rtl, hapticFeedbackEnabled) {
                detectTapGestures { offset ->
                    val x = startX(offset.x, size.width)
                    val tapped = snapRating(ratingAtPosition(x, itemPx, spacingPx, maxRating), stepSize, minRating, maxRating, roundUp = true)
                    val value = if (allowClear && tapped == currentRating) minRating else tapped
                    if (value != currentRating) {
                        animator.tapPending = true
                        if (hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                        currentOnChange?.invoke(value)
                    } else {
                        animator.acknowledge(itemAtPosition(x, itemPx, spacingPx, maxRating), currentAnimation)
                    }
                    currentOnFinished?.invoke()
                }
            }
            .pointerInput(maxRating, stepSize, minRating, itemPx, spacingPx, rtl, hapticFeedbackEnabled) {
                var last = 0f
                fun update(position: Offset) {
                    val x = startX(position.x, size.width)
                    animator.setDragIndex(itemAtPosition(x, itemPx, spacingPx, maxRating), currentAnimation)
                    val value = snapRating(ratingAtPosition(x, itemPx, spacingPx, maxRating), stepSize, minRating, maxRating, roundUp = true)
                    if (value == last) return
                    // Free ratings change every pixel; only tick when a whole item is crossed.
                    val tick = if (stepSize > 0f) true else floor(value) != floor(last)
                    last = value
                    if (tick && hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    currentOnChange?.invoke(value)
                }
                fun finish() {
                    animator.dragging = false
                    animator.setDragIndex(-1, currentAnimation)
                    currentOnFinished?.invoke()
                }
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        animator.dragging = true
                        last = currentRating
                        update(offset)
                    },
                    onDragEnd = ::finish,
                    onDragCancel = ::finish,
                ) { change, _ ->
                    change.consume()
                    update(change.position)
                }
            }
    } else {
        Modifier
    }

    val stateText = stringResource(R.string.ccrb_state_description, formatRating(displayRating, currentLocale()), maxRating)
    val steps = if (stepSize > 0f) ((maxRating / stepSize).roundToInt() - 1).coerceAtLeast(0) else 0

    Spacer(
        modifier
            .semantics {
                stateDescription = stateText
                progressBarRangeInfo = ProgressBarRangeInfo(displayRating, 0f..maxRating.toFloat(), steps)
                if (!enabled) disabled()
                if (interactive) {
                    setProgress { target ->
                        val value = snapRating(target, stepSize, minRating, maxRating, roundUp = false)
                        if (value != currentRating) currentOnChange?.invoke(value)
                        currentOnFinished?.invoke()
                        true
                    }
                }
            }
            .then(gestures)
            .size(width = itemSize * maxRating + itemSpacing * (maxRating - 1), height = itemSize)
            .drawWithCache {
                val strokePx = borderWidth.toPx()
                // Inset by half the stroke so borders stay inside the item.
                val path = Path().apply {
                    val inner = (itemPx - strokePx).coerceAtLeast(0f)
                    addOutline(shape.createOutline(Size(inner, inner), layoutDirection, this@drawWithCache))
                    translate(Offset(strokePx / 2f, strokePx / 2f))
                }
                val stroke = Stroke(width = strokePx, join = StrokeJoin.Round)
                val itemSizePx = Size(itemPx, itemPx)

                fun DrawScope.drawItem(fill: Color, border: Color) {
                    if (painter != null) {
                        with(painter) { draw(itemSizePx, colorFilter = ColorFilter.tint(fill)) }
                    } else {
                        if (fill.alpha > 0f) drawPath(path, fill)
                        if (strokePx > 0f && border.alpha > 0f) drawPath(path, border, style = stroke)
                    }
                }

                onDrawBehind {
                    val top = (size.height - itemPx) / 2f
                    fun itemLeft(i: Int): Float {
                        val start = i * (itemPx + spacingPx)
                        return if (rtl) size.width - start - itemPx else start
                    }

                    val haloIndex = animator.haloIndex
                    val haloProgress = animator.halo.value
                    if (haloIndex in 0 until maxRating && haloProgress < 1f) {
                        drawCircle(
                            color = haloColor,
                            radius = itemPx / 2f * (0.9f + 0.9f * haloProgress),
                            center = Offset(itemLeft(haloIndex) + itemPx / 2f, top + itemPx / 2f),
                            alpha = 0.32f * (1f - haloProgress),
                        )
                    }

                    for (i in 0 until maxRating) {
                        val fraction = animator.fills[i].value
                        val scale = animator.scales[i].value
                        translate(itemLeft(i), top) {
                            scale(scale, pivot = Offset(itemPx / 2f, itemPx / 2f)) {
                                // Split point between the filled and empty parts, measured from the left.
                                val split = itemPx * if (rtl) 1f - fraction else fraction
                                val filledLeft = if (rtl) split else 0f
                                val filledRight = if (rtl) itemPx else split
                                if (fraction > 0f) {
                                    clipRect(filledLeft, 0f, filledRight, itemPx) { drawItem(activeColor, activeBorderColor) }
                                }
                                if (fraction < 1f) {
                                    val emptyLeft = if (rtl) 0f else split
                                    val emptyRight = if (rtl) split else itemPx
                                    clipRect(emptyLeft, 0f, emptyRight, itemPx) { drawItem(inactiveColor, inactiveBorderColor) }
                                }
                            }
                        }
                    }
                }
            },
    )
}
