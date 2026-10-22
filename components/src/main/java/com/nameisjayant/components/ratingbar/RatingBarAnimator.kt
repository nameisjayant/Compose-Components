package com.nameisjayant.components.ratingbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Drives the per-item fill, scale and halo of a [ComposeRatingBar]. Values are read in the draw phase only,
 * so animating never recomposes the bar.
 *
 * Jobs run in the composition's scope (not in a LaunchedEffect) so a new rating doesn't cut off items that
 * are still settling; each item's previous job is cancelled instead.
 */
@Stable
internal class RatingBarAnimator(
    count: Int,
    initialRating: Float,
    private val scope: CoroutineScope,
) {
    val fills: List<Animatable<Float, AnimationVector1D>> = List(count) { Animatable(fractionOf(initialRating, it)) }
    val scales: List<Animatable<Float, AnimationVector1D>> = List(count) { Animatable(1f) }

    /** 0 → 1 while the halo expands and fades; 1 = hidden. */
    val halo: Animatable<Float, AnimationVector1D> = Animatable(1f)
    var haloIndex: Int by mutableIntStateOf(-1)
        private set

    /** Set by the gesture handlers. */
    var dragging: Boolean = false
    var tapPending: Boolean = false

    private var current = initialRating
    private var dragIndex = -1
    private val fillJobs = arrayOfNulls<Job>(count)
    private val scaleJobs = arrayOfNulls<Job>(count)
    private var haloJob: Job? = null

    fun animateTo(target: Float, animation: RatingBarAnimation) {
        val previous = current
        current = target
        val celebrate = tapPending
        tapPending = false

        if (!animation.enabled) {
            fills.forEachIndexed { i, fill ->
                fillJobs[i]?.cancel()
                fillJobs[i] = scope.launch { fill.snapTo(fractionOf(target, i)) }
            }
            return
        }

        val increasing = target > previous
        val stagger = if (dragging) 0L else animation.staggerMillis
        val touched = ceil(target).toInt() - 1
        // Items fill outwards from the old rating and empty back towards the new one.
        fun orderOf(i: Int): Int =
            (if (increasing) i - floor(previous).toInt() else ceil(previous).toInt() - 1 - i).coerceAtLeast(0)

        fills.forEachIndexed { i, fill ->
            val goal = fractionOf(target, i)
            fillJobs[i]?.cancel()
            if (fill.value == goal) return@forEachIndexed
            val delayMillis = orderOf(i) * stagger
            fillJobs[i] = scope.launch {
                delay(delayMillis)
                fill.animateTo(goal, if (dragging) DragFillSpec else FillSpec)
            }
            if (!dragging && increasing && goal > fill.value) {
                pulse(i, delayMillis, if (celebrate && i == touched) animation.popScale else animation.waveScale)
            }
        }

        if (celebrate && !dragging && touched >= 0) {
            val delayMillis = if (increasing) orderOf(touched) * stagger else 0L
            // Lowering the rating fills nothing, so the tapped item still needs its pop.
            if (!increasing) pulse(touched, 0L, animation.popScale)
            if (animation.showHalo) showHalo(touched, delayMillis)
        }
    }

    /** Lifts the item under the finger while dragging; -1 settles everything back. */
    fun setDragIndex(index: Int, animation: RatingBarAnimation) {
        if (index == dragIndex) return
        val previous = dragIndex
        dragIndex = index
        if (!animation.enabled) return
        if (previous >= 0) settle(previous)
        if (index >= 0) {
            scaleJobs[index]?.cancel()
            scaleJobs[index] = scope.launch { scales[index].animateTo(animation.dragScale, LiftSpec) }
        }
    }

    /** A tap that didn't change the rating still gets a small acknowledgement. */
    fun acknowledge(index: Int, animation: RatingBarAnimation) {
        if (animation.enabled && index >= 0) pulse(index, 0L, animation.waveScale)
    }

    private fun pulse(index: Int, delayMillis: Long, peak: Float) {
        scaleJobs[index]?.cancel()
        scaleJobs[index] = scope.launch {
            delay(delayMillis)
            scales[index].animateTo(peak, LiftSpec)
            scales[index].animateTo(1f, SettleSpec)
        }
    }

    private fun settle(index: Int) {
        scaleJobs[index]?.cancel()
        scaleJobs[index] = scope.launch { scales[index].animateTo(1f, SettleSpec) }
    }

    private fun showHalo(index: Int, delayMillis: Long) {
        haloJob?.cancel()
        haloJob = scope.launch {
            delay(delayMillis)
            haloIndex = index
            halo.snapTo(0f)
            halo.animateTo(1f, tween(durationMillis = 650, easing = LinearOutSlowInEasing))
        }
    }

    private companion object {
        val FillSpec = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
        val DragFillSpec = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f)
        val LiftSpec = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1100f)
        val SettleSpec = spring<Float>(dampingRatio = 0.42f, stiffness = 380f)
    }
}
