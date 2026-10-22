package com.nameisjayant.components.wheelpicker

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Holds which item a [ComposeWheelPicker] shows and lets you spin it from code.
 *
 * [selectedIndex] follows the wheel live while it turns. Use one state per wheel.
 *
 * ```
 * val state = rememberComposeWheelPickerState(initialIndex = 7)
 * ComposeWheelPicker(items = hours, state = state)
 * scope.launch { state.animateScrollToIndex(0) }
 * ```
 */
@Stable
public class ComposeWheelPickerState(initialIndex: Int = 0) {

    init {
        require(initialIndex >= 0) { "initialIndex can't be negative, was $initialIndex" }
    }

    internal val listState: LazyListState = LazyListState()

    // Set by the picker; until it lays the items out, [selectedIndex] reports [pendingIndex].
    internal var itemCount: Int by mutableIntStateOf(0)
        private set
    internal var cyclic: Boolean by mutableStateOf(false)
        private set
    private var pendingIndex by mutableIntStateOf(initialIndex)
    private var attached = false

    /** Last index reported through `onSelectedIndexChange`; only touched by the picker's effects. */
    internal var settledIndex: Int = initialIndex

    /** Row of the underlying list under the selection line, or -1 before the list has laid out these items. */
    internal val centerRawIndex: Int by derivedStateOf {
        val count = itemCount
        val info = listState.layoutInfo
        val rawCount = wheelRawCount(count, cyclic)
        val itemPx = info.visibleItemsInfo.firstOrNull()?.size ?: 0
        if (count == 0 || info.totalItemsCount != rawCount || itemPx == 0) {
            -1
        } else {
            wheelCenterRawIndex(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset, itemPx)
                .coerceAtMost(rawCount - 1)
        }
    }

    /** Index of the item under the selection line; changes as the wheel turns. */
    public val selectedIndex: Int
        get() {
            val raw = centerRawIndex
            return if (raw < 0) pendingIndex else wheelLogicalIndex(raw, itemCount)
        }

    /** Whether the wheel is turning, by a drag, a fling or one of the scroll functions. */
    public val isScrollInProgress: Boolean get() = listState.isScrollInProgress

    /** Jumps straight to item [index]. */
    public suspend fun scrollToIndex(index: Int) {
        if (itemCount == 0) {
            pendingIndex = index.coerceAtLeast(0)
            return
        }
        listState.scrollToItem(rawIndexFor(index))
    }

    /** Spins the wheel to item [index]; a cyclic wheel goes the short way round. */
    public suspend fun animateScrollToIndex(index: Int) {
        if (itemCount == 0) {
            pendingIndex = index.coerceAtLeast(0)
            return
        }
        listState.animateScrollToItem(rawIndexFor(index))
    }

    internal suspend fun animateScrollToRaw(raw: Int) {
        listState.animateScrollToItem(raw)
    }

    private fun rawIndexFor(index: Int): Int {
        val current = centerRawIndex.takeIf { it >= 0 } ?: listState.firstVisibleItemIndex
        return wheelNearestRawIndex(current, index, itemCount, cyclic)
    }

    /**
     * Called by the picker after each composition. When the items change it keeps the selected item
     * (or the last one, if the list got shorter) under the selection line.
     */
    internal fun update(count: Int, cyclic: Boolean) {
        if (attached && count == itemCount && cyclic == this.cyclic) return
        val index = (if (attached) selectedIndex else pendingIndex).coerceIn(0, (count - 1).coerceAtLeast(0))
        pendingIndex = index
        itemCount = count
        this.cyclic = cyclic
        attached = true
        if (count > 0) listState.requestScrollToItem(wheelStartRawIndex(index, count, cyclic))
    }

    public companion object {
        /** Saves the selected index. */
        public val Saver: Saver<ComposeWheelPickerState, Int> = Saver(
            save = { it.selectedIndex },
            restore = { ComposeWheelPickerState(initialIndex = it) },
        )
    }
}

/** A [ComposeWheelPickerState] that survives configuration changes and process death. */
@Composable
public fun rememberComposeWheelPickerState(initialIndex: Int = 0): ComposeWheelPickerState =
    rememberSaveable(saver = ComposeWheelPickerState.Saver) { ComposeWheelPickerState(initialIndex) }
