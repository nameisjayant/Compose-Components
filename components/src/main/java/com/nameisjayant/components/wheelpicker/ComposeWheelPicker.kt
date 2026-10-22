package com.nameisjayant.components.wheelpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A scrolling wheel for picking one item from a list: numbers, times, months, anything. Drag or fling
 * it and it snaps so one row rests on the selection line; tap a row to spin to it.
 *
 * This overload is driven by [state], which also lets you spin the wheel from code and read
 * [ComposeWheelPickerState.selectedIndex] live while it turns. For a plain value/callback pair use the
 * overload that takes `selectedIndex`.
 *
 * ```
 * val minutes = (0..59).map { "%02d".format(it) }
 * val state = rememberComposeWheelPickerState(initialIndex = 30)
 * ComposeWheelPicker(items = minutes, state = state, cyclic = true)
 * ```
 *
 * To line several wheels up as one picker (hours : minutes), put them in a `Row`, turn their own
 * indicator off with `selectionIndicator = null`, and draw one [ComposeWheelPickerDefaults.SelectionBand]
 * behind the row.
 *
 * @param items what the wheel offers; it may change, and the wheel keeps its selected item (or the last
 * one, if the list got shorter).
 * @param modifier size the wheel's width here; without a width it is [ComposeWheelPickerDefaults.Width]
 * wide. Its height is always [itemHeight] × [visibleItemCount].
 * @param state see [rememberComposeWheelPickerState].
 * @param onSelectedIndexChange called with the new index once the wheel comes to rest on a different
 * item (also after a scroll from code, or when a shorter [items] list moves the selection).
 * @param enabled when false the wheel ignores touch and uses the disabled colors.
 * @param cyclic the list wraps round, so the wheel spins endlessly in both directions.
 * @param visibleItemCount rows shown at once; must be odd so one sits in the middle.
 * @param itemHeight height of each row.
 * @param style curved or flat rows, and how much they fade and shrink away from the center.
 * @param textStyle style of the row labels; its color is replaced by [colors].
 * @param selectedTextStyle style of the selected row's label, e.g. a bolder weight.
 * @param colors see [ComposeWheelPickerDefaults.colors].
 * @param selectionIndicator drawn behind the selected row, one row tall; see
 * [ComposeWheelPickerDefaults.SelectionBand] and [ComposeWheelPickerDefaults.SelectionLines]. null draws none.
 * @param fadeEdges fade rows out at the top and bottom edges of the wheel.
 * @param isItemEnabled items it returns false for are dimmed and skipped: the wheel won't rest on them
 * and moves on to the nearest enabled item.
 * @param selectOnTap tapping a row spins the wheel to it.
 * @param hapticFeedbackEnabled a light tick each time a new row crosses the selection line.
 * @param flingBehavior how a fling decays and snaps; the default lands each fling on a row.
 * @param contentDescription what the wheel picks, e.g. "Hours", read out by screen readers along with
 * the selected item.
 * @param itemLabel the text each row shows; also read out by screen readers.
 * @param itemContent draw each row yourself from its [WheelPickerItem] instead of the default label. It
 * is placed in a centered box one row tall, still curves and fades with the wheel, and gets the
 * selected or plain text color as [LocalContentColor].
 */
@Composable
public fun <T> ComposeWheelPicker(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: ComposeWheelPickerState = rememberComposeWheelPickerState(),
    onSelectedIndexChange: ((Int) -> Unit)? = null,
    enabled: Boolean = true,
    cyclic: Boolean = false,
    visibleItemCount: Int = ComposeWheelPickerDefaults.VisibleItemCount,
    itemHeight: Dp = ComposeWheelPickerDefaults.ItemHeight,
    style: WheelPickerStyle = WheelPickerStyle.Wheel,
    textStyle: TextStyle = ComposeWheelPickerDefaults.textStyle(),
    selectedTextStyle: TextStyle = textStyle,
    colors: ComposeWheelPickerColors = ComposeWheelPickerDefaults.colors(),
    selectionIndicator: (@Composable () -> Unit)? = {
        ComposeWheelPickerDefaults.SelectionBand(color = colors.indicatorColor)
    },
    fadeEdges: Boolean = true,
    isItemEnabled: (index: Int) -> Boolean = { true },
    selectOnTap: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    flingBehavior: FlingBehavior = ComposeWheelPickerDefaults.flingBehavior(state),
    contentDescription: String? = null,
    itemLabel: (T) -> String = { it.toString() },
    itemContent: (@Composable (WheelPickerItem<T>) -> Unit)? = null,
) {
    require(visibleItemCount >= 1 && visibleItemCount % 2 == 1) {
        "visibleItemCount must be a positive odd number, was $visibleItemCount"
    }
    require(itemHeight > 0.dp) { "itemHeight must be positive, was $itemHeight" }

    val count = items.size
    val rawCount = wheelRawCount(count, cyclic)
    val listState = state.listState
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val itemPx = with(LocalDensity.current) { itemHeight.toPx() }

    SideEffect { state.update(count, cyclic) }

    val currentIsItemEnabled by rememberUpdatedState(isItemEnabled)
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val currentHaptics by rememberUpdatedState(hapticFeedbackEnabled)

    // A tick for every row that crosses the selection line while the wheel turns.
    LaunchedEffect(state) {
        var last = -1
        snapshotFlow { state.centerRawIndex }.collect { raw ->
            if (raw >= 0 && last >= 0 && raw != last && listState.isScrollInProgress && currentHaptics) {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            last = raw
        }
    }

    // Once the wheel rests: move off a disabled item, otherwise report a new selection.
    LaunchedEffect(state, count, cyclic) {
        snapshotFlow { listState.isScrollInProgress to state.selectedIndex }.collect { (scrolling, index) ->
            if (scrolling || count == 0 || state.centerRawIndex < 0) return@collect
            if (!currentIsItemEnabled(index)) {
                val target = wheelNearestEnabledIndex(index, count, cyclic, currentIsItemEnabled)
                if (target != null && target != index) {
                    // Collecting must not suspend, or the next settle could be missed; a touch may cancel this.
                    launch {
                        try {
                            state.animateScrollToIndex(target)
                        } catch (_: CancellationException) {
                        }
                    }
                }
                return@collect
            }
            if (index != state.settledIndex) {
                state.settledIndex = index
                currentOnSelectedIndexChange?.invoke(index)
            }
        }
    }

    val selected = state.selectedIndex.coerceIn(0, (count - 1).coerceAtLeast(0))
    val stateText = items.getOrNull(selected)?.let(itemLabel)
    val half = visibleItemCount / 2

    Box(
        modifier
            .width(ComposeWheelPickerDefaults.Width)
            .height(itemHeight * visibleItemCount)
            .clearAndSetSemantics {
                if (contentDescription != null) this.contentDescription = contentDescription
                if (stateText != null) stateDescription = stateText
                if (count > 1) {
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = selected.toFloat(),
                        range = 0f..(count - 1).toFloat(),
                        steps = count - 2,
                    )
                }
                if (!enabled) {
                    disabled()
                } else if (count > 0) {
                    setProgress { target ->
                        val index = target.roundToInt().coerceIn(0, count - 1)
                        scope.launch { state.animateScrollToIndex(index) }
                        true
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selectionIndicator != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
            ) { selectionIndicator() }
        }

        LazyColumn(
            modifier = Modifier
                .matchParentSize()
                .then(if (fadeEdges) Modifier.fadeEdges(visibleItemCount) else Modifier),
            state = listState,
            contentPadding = PaddingValues(vertical = itemHeight * half),
            flingBehavior = flingBehavior,
            userScrollEnabled = enabled && count > 0,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(count = rawCount) { raw ->
                val index = wheelLogicalIndex(raw, count)
                val item = items[index]
                val itemEnabled = enabled && currentIsItemEnabled(index)
                val isSelected by remember(state, raw) {
                    derivedStateOf {
                        val center = state.centerRawIndex
                        if (center >= 0) center == raw else wheelLogicalIndex(raw, state.itemCount) == state.selectedIndex
                    }
                }
                val textColor = remember(listState, raw, itemPx, colors, enabled, itemEnabled) {
                    ColorProducer {
                        when {
                            !enabled -> colors.disabledTextColor
                            !itemEnabled -> colors.disabledItemTextColor
                            else -> {
                                val closeness = 1f - abs(listState.distanceFromCenter(raw, itemPx)).coerceAtMost(1f)
                                lerp(colors.textColor, colors.selectedTextColor, closeness)
                            }
                        }
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer { wheelRow(listState.distanceFromCenter(raw, itemPx), visibleItemCount, itemPx, style) }
                        .clickable(
                            enabled = selectOnTap && itemEnabled,
                            interactionSource = null,
                            indication = null,
                        ) { scope.launch { state.animateScrollToRaw(raw) } },
                    contentAlignment = Alignment.Center,
                ) {
                    if (itemContent != null) {
                        val contentColor = when {
                            !enabled -> colors.disabledTextColor
                            !itemEnabled -> colors.disabledItemTextColor
                            isSelected -> colors.selectedTextColor
                            else -> colors.textColor
                        }
                        CompositionLocalProvider(LocalContentColor provides contentColor) {
                            itemContent(WheelPickerItem(index, item, isSelected, itemEnabled))
                        }
                    } else {
                        BasicText(
                            text = itemLabel(item),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = if (isSelected) selectedTextStyle else textStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }
}

/**
 * A wheel picker driven by a plain index: show [selectedIndex] and update it in
 * [onSelectedIndexChange]. Changing [selectedIndex] from outside spins the wheel to it.
 *
 * ```
 * var month by rememberSaveable { mutableIntStateOf(0) }
 * ComposeWheelPicker(items = months, selectedIndex = month, onSelectedIndexChange = { month = it })
 * ```
 *
 * Every other parameter works as in the [ComposeWheelPickerState] overload.
 */
@Composable
public fun <T> ComposeWheelPicker(
    items: List<T>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cyclic: Boolean = false,
    visibleItemCount: Int = ComposeWheelPickerDefaults.VisibleItemCount,
    itemHeight: Dp = ComposeWheelPickerDefaults.ItemHeight,
    style: WheelPickerStyle = WheelPickerStyle.Wheel,
    textStyle: TextStyle = ComposeWheelPickerDefaults.textStyle(),
    selectedTextStyle: TextStyle = textStyle,
    colors: ComposeWheelPickerColors = ComposeWheelPickerDefaults.colors(),
    selectionIndicator: (@Composable () -> Unit)? = {
        ComposeWheelPickerDefaults.SelectionBand(color = colors.indicatorColor)
    },
    fadeEdges: Boolean = true,
    isItemEnabled: (index: Int) -> Boolean = { true },
    selectOnTap: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    contentDescription: String? = null,
    itemLabel: (T) -> String = { it.toString() },
    itemContent: (@Composable (WheelPickerItem<T>) -> Unit)? = null,
) {
    val state = rememberComposeWheelPickerState(initialIndex = selectedIndex.coerceAtLeast(0))
    LaunchedEffect(state, selectedIndex) {
        if (selectedIndex in items.indices && selectedIndex != state.selectedIndex) {
            state.animateScrollToIndex(selectedIndex)
        }
    }
    ComposeWheelPicker(
        items = items,
        modifier = modifier,
        state = state,
        onSelectedIndexChange = onSelectedIndexChange,
        enabled = enabled,
        cyclic = cyclic,
        visibleItemCount = visibleItemCount,
        itemHeight = itemHeight,
        style = style,
        textStyle = textStyle,
        selectedTextStyle = selectedTextStyle,
        colors = colors,
        selectionIndicator = selectionIndicator,
        fadeEdges = fadeEdges,
        isItemEnabled = isItemEnabled,
        selectOnTap = selectOnTap,
        hapticFeedbackEnabled = hapticFeedbackEnabled,
        contentDescription = contentDescription,
        itemLabel = itemLabel,
        itemContent = itemContent,
    )
}

/**
 * Rows between row [raw] and the selection line: negative above it, positive below. Read in the draw
 * phase, so the rows don't recompose while the wheel turns.
 */
private fun LazyListState.distanceFromCenter(raw: Int, itemPx: Float): Float =
    (raw - firstVisibleItemIndex).toFloat() - firstVisibleItemScrollOffset / itemPx

/** Curves, fades and shrinks a row [distance] rows from the selection line. */
private fun GraphicsLayerScope.wheelRow(distance: Float, visibleItemCount: Int, itemPx: Float, style: WheelPickerStyle) {
    val edge = wheelEdgeFraction(distance, visibleItemCount)
    val scale = lerpFloat(1f, style.outerScale, edge)
    scaleX = scale
    scaleY = scale
    if (style.curved) {
        if (wheelRowHidden(distance, visibleItemCount)) {
            alpha = 0f
            return
        }
        // Tip rows back round the drum and pull them in towards the center, as a cylinder would.
        rotationX = -Math.toDegrees(wheelAngle(distance, visibleItemCount).toDouble()).toFloat()
        translationY = wheelCurveShift(distance, visibleItemCount) * itemPx
    }
    alpha = lerpFloat(1f, style.outerAlpha, edge)
}

private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

/** Fades the content out over the outer three quarters of a row at the top and bottom. */
private fun Modifier.fadeEdges(visibleItemCount: Int): Modifier {
    val fade = 0.75f / visibleItemCount
    val mask = Brush.verticalGradient(
        0f to Color.Transparent,
        fade to Color.Black,
        1f - fade to Color.Black,
        1f to Color.Transparent,
    )
    return graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(mask, blendMode = BlendMode.DstIn)
        }
}
