package com.nameisjayant.components.wheelpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [ComposeWheelPicker]. */
public object ComposeWheelPickerDefaults {

    /** Rows shown at once: the selected one and two either side. */
    public const val VisibleItemCount: Int = 5

    public val ItemHeight: Dp = 48.dp

    /** Width of a picker whose modifier doesn't size it, e.g. one in a `Row` without `weight`. */
    public val Width: Dp = 96.dp

    public val IndicatorShape: Shape = RoundedCornerShape(12.dp)
    public val IndicatorLineThickness: Dp = 1.dp

    /** Centered [MaterialTheme.typography] title text. */
    @Composable
    @ReadOnlyComposable
    public fun textStyle(): TextStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)

    /** Colors from [MaterialTheme.colorScheme]; any of them can be overridden. */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledItemTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        indicatorColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        indicatorLineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    ): ComposeWheelPickerColors = ComposeWheelPickerColors(
        textColor = textColor,
        selectedTextColor = selectedTextColor,
        disabledItemTextColor = disabledItemTextColor,
        disabledTextColor = disabledTextColor,
        indicatorColor = indicatorColor,
        indicatorLineColor = indicatorLineColor,
    )

    /** Selected text, band and lines in [accent]; everything else follows [MaterialTheme]. */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(accent: Color): ComposeWheelPickerColors = colors(
        selectedTextColor = accent,
        indicatorColor = accent.copy(alpha = 0.12f),
        indicatorLineColor = accent.copy(alpha = 0.55f),
    )

    /** Snaps the wheel so a row always comes to rest on the selection line. */
    @Composable
    public fun flingBehavior(state: ComposeWheelPickerState): FlingBehavior = rememberSnapFlingBehavior(state.listState)

    /**
     * A rounded band behind the selected row; the picker's default indicator.
     *
     * To share one band across several wheels in a `Row` (a time picker, say), turn their own indicator
     * off with `selectionIndicator = null` and place this behind the row, one item tall.
     */
    @Composable
    public fun SelectionBand(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape: Shape = IndicatorShape,
    ) {
        Box(modifier.fillMaxSize().background(color, shape))
    }

    /** Two thin lines above and below the selected row. Can be shared across wheels like [SelectionBand]. */
    @Composable
    public fun SelectionLines(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.outlineVariant,
        thickness: Dp = IndicatorLineThickness,
    ) {
        Box(modifier.fillMaxSize()) {
            Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(thickness).background(color))
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(thickness).background(color))
        }
    }
}
