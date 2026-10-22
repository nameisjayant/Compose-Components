package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.nameisjayant.components.R
import kotlin.math.roundToInt

/**
 * A slider through every hue, red to red. Changes only [HsvColor.hue].
 *
 * Like all the picker's parts it is stateless: pass the current color and store what
 * [onHsvColorChange] reports, e.g. `HueSlider(state.hsvColor, { state.hsvColor = it })`.
 *
 * @param onColorChangeFinished called when the finger lifts, or after an accessibility change.
 */
@Composable
public fun HueSlider(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    trackHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val current by rememberUpdatedState(hsvColor)
    ColorSlider(
        fraction = hsvColor.hue / 360f,
        onFractionChange = { onHsvColorChange(current.copy(hue = it * 360f)) },
        trackColors = HueStops,
        thumbFill = Color(hsvToArgb(hsvColor.hue, 1f, 1f, 1f)),
        label = stringResource(R.string.cccp_hue),
        valueRange = 0f..360f,
        stateDescription = stringResource(R.string.cccp_degrees, hsvColor.hue.roundToInt()),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        trackHeight = trackHeight,
        thumbSize = thumbSize,
        onChangeFinished = onColorChangeFinished,
    )
}

/** A slider from gray to the full color. Changes only [HsvColor.saturation]. See [HueSlider]. */
@Composable
public fun SaturationSlider(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    trackHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val current by rememberUpdatedState(hsvColor)
    ColorSlider(
        fraction = hsvColor.saturation,
        onFractionChange = { onHsvColorChange(current.copy(saturation = it)) },
        trackColors = listOf(hsvColor.copy(saturation = 0f).opaque(), hsvColor.copy(saturation = 1f).opaque()),
        thumbFill = hsvColor.opaque(),
        label = stringResource(R.string.cccp_saturation),
        valueRange = 0f..1f,
        stateDescription = percent(hsvColor.saturation),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        trackHeight = trackHeight,
        thumbSize = thumbSize,
        onChangeFinished = onColorChangeFinished,
    )
}

/** A slider from black to the full-brightness color. Changes only [HsvColor.value]. See [HueSlider]. */
@Composable
public fun BrightnessSlider(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    trackHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val current by rememberUpdatedState(hsvColor)
    ColorSlider(
        fraction = hsvColor.value,
        onFractionChange = { onHsvColorChange(current.copy(value = it)) },
        trackColors = listOf(Color.Black, hsvColor.copy(value = 1f).opaque()),
        thumbFill = hsvColor.opaque(),
        label = stringResource(R.string.cccp_brightness),
        valueRange = 0f..1f,
        stateDescription = percent(hsvColor.value),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        trackHeight = trackHeight,
        thumbSize = thumbSize,
        onChangeFinished = onColorChangeFinished,
    )
}

/**
 * A slider from transparent to opaque over a checkerboard. Changes only [HsvColor.alpha]. See [HueSlider].
 */
@Composable
public fun AlphaSlider(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    trackHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val current by rememberUpdatedState(hsvColor)
    ColorSlider(
        fraction = hsvColor.alpha,
        onFractionChange = { onHsvColorChange(current.copy(alpha = it)) },
        trackColors = listOf(hsvColor.copy(alpha = 0f).toColor(), hsvColor.opaque()),
        thumbFill = hsvColor.toColor(),
        label = stringResource(R.string.cccp_alpha),
        valueRange = 0f..1f,
        stateDescription = percent(hsvColor.alpha),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        trackHeight = trackHeight,
        thumbSize = thumbSize,
        onChangeFinished = onColorChangeFinished,
        checkerboard = true,
    )
}

@Composable
private fun percent(fraction: Float): String = stringResource(R.string.cccp_percent, (fraction * 100f).roundToInt())

/**
 * The slider all of the above share: a pill track painted with [trackColors] and a thumb at [fraction]
 * (0..1). The thumb's center stays a thumb radius inside each end, and the gradient spans that same
 * range, so the color under the thumb is the color it picks.
 */
@Composable
private fun ColorSlider(
    fraction: Float,
    onFractionChange: (Float) -> Unit,
    trackColors: List<Color>,
    thumbFill: Color,
    label: String,
    valueRange: ClosedFloatingPointRange<Float>,
    stateDescription: String,
    modifier: Modifier,
    enabled: Boolean,
    colors: ComposeColorPickerColors,
    trackHeight: Dp,
    thumbSize: Dp,
    onChangeFinished: (() -> Unit)?,
    checkerboard: Boolean = false,
) {
    require(trackHeight > 0.dp) { "trackHeight must be positive, was $trackHeight" }
    val onChange by rememberUpdatedState(onFractionChange)
    val onFinished by rememberUpdatedState(onChangeFinished)

    Canvas(
        modifier
            .fillMaxWidth()
            .height(max(trackHeight, thumbSize))
            .alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha)
            .semantics {
                contentDescription = label
                this.stateDescription = stateDescription
                val span = valueRange.endInclusive - valueRange.start
                progressBarRangeInfo = ProgressBarRangeInfo(valueRange.start + fraction * span, valueRange)
                if (enabled) {
                    setProgress { target ->
                        onChange(((target - valueRange.start) / span).coerceIn(0f, 1f))
                        onFinished?.invoke()
                        true
                    }
                } else {
                    disabled()
                }
            }
            .colorDrag(enabled, onFinished = { onFinished?.invoke() }) { position ->
                val radius = thumbSize.toPx() / 2f
                val travel = size.width - 2f * radius
                if (travel > 0f) onChange(((position.x - radius) / travel).coerceIn(0f, 1f))
            },
    ) {
        val radius = thumbSize.toPx() / 2f
        val trackPx = trackHeight.toPx().coerceAtMost(size.height)
        val top = (size.height - trackPx) / 2f
        val track = Path().apply {
            addRoundRect(RoundRect(0f, top, size.width, top + trackPx, CornerRadius(trackPx / 2f)))
        }
        val start = radius.coerceAtMost(size.width / 2f)
        val end = (size.width - radius).coerceAtLeast(start)

        if (checkerboard) {
            clipPath(track) { drawCheckerboard(colors.checkerboardLightColor, colors.checkerboardDarkColor) }
        }
        drawPath(track, Brush.horizontalGradient(trackColors, startX = start, endX = end))
        drawPath(track, colors.borderColor, style = Stroke(1.dp.toPx()))
        drawColorThumb(
            center = Offset(start + fraction.coerceIn(0f, 1f) * (end - start), size.height / 2f),
            radius = radius,
            fill = thumbFill,
            colors = colors,
        )
    }
}
