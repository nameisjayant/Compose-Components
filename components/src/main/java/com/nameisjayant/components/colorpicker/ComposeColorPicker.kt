package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A color picker: a hue/saturation/brightness area in one of three [style]s, then optional alpha
 * slider, preview, hex field and preset swatches, top to bottom.
 *
 * This overload is driven by [state], which holds the color (hue included, so grays don't forget
 * it) and lets you set it from code. For a plain value/callback pair use the overload that takes `color`.
 *
 * ```
 * val state = rememberComposeColorPickerState(initialColor = brandColor)
 * ComposeColorPicker(state, style = ColorPickerStyle.Ring, onColorChangeFinished = { save(it) })
 * ```
 *
 * Every part is also public ([HueSaturationWheel], [HueRingPanel], [SaturationValuePanel],
 * [HueSlider], [SaturationSlider], [BrightnessSlider], [AlphaSlider], [ColorPreview], [ColorHexField],
 * [ColorSwatches]) for a layout of your own.
 *
 * @param state see [rememberComposeColorPickerState].
 * @param modifier the picker fills the width it is given; size it here.
 * @param onColorChange called with each new color while it changes (every drag step).
 * @param onColorChangeFinished called with the color when a change ends: a finger lifts, a swatch is
 * tapped or a hex code is entered. Handy for saving or for a "recent colors" list.
 * @param style the main color area; see [ColorPickerStyle].
 * @param enabled when false nothing responds to touch and the picker is dimmed.
 * @param showAlpha show the alpha slider, and alpha in the hex field. When hidden, the color keeps the
 * alpha it has.
 * @param showPreview show a swatch of the current color next to the hex field.
 * @param showHexField show a field to read and type the hex code.
 * @param originalColor when given, the preview shows it beside the current color, and tapping it goes
 * back to it. Pass the color the picker opened with.
 * @param swatches preset colors under the picker; empty for none.
 * @param colors see [ComposeColorPickerDefaults.colors].
 * @param thumbSize diameter of the thumbs.
 * @param sliderHeight thickness of the slider tracks.
 * @param ringWidth thickness of the hue ring in [ColorPickerStyle.Ring].
 * @param panelShape corners of the saturation/brightness panel ([ColorPickerStyle.Square] and [ColorPickerStyle.Ring]).
 * @param swatchSize size of each preset swatch.
 * @param spacing gap between the picker's sections.
 */
@Composable
public fun ComposeColorPicker(
    state: ComposeColorPickerState,
    modifier: Modifier = Modifier,
    onColorChange: ((Color) -> Unit)? = null,
    onColorChangeFinished: ((Color) -> Unit)? = null,
    style: ColorPickerStyle = ColorPickerStyle.Wheel,
    enabled: Boolean = true,
    showAlpha: Boolean = true,
    showPreview: Boolean = true,
    showHexField: Boolean = true,
    originalColor: Color? = null,
    swatches: List<Color> = ComposeColorPickerDefaults.Swatches,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    sliderHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    ringWidth: Dp = ComposeColorPickerDefaults.RingWidth,
    panelShape: Shape = ComposeColorPickerDefaults.PanelShape,
    swatchSize: Dp = ComposeColorPickerDefaults.SwatchSize,
    spacing: Dp = ComposeColorPickerDefaults.Spacing,
) {
    ColorPickerContent(
        hsvColor = state.hsvColor,
        onHsvColorChange = {
            state.hsvColor = it
            onColorChange?.invoke(it.toColor())
        },
        onChangeFinished = { onColorChangeFinished?.invoke(state.color) },
        modifier = modifier,
        style = style,
        enabled = enabled,
        showAlpha = showAlpha,
        showPreview = showPreview,
        showHexField = showHexField,
        originalColor = originalColor,
        swatches = swatches,
        colors = colors,
        thumbSize = thumbSize,
        sliderHeight = sliderHeight,
        ringWidth = ringWidth,
        panelShape = panelShape,
        swatchSize = swatchSize,
        spacing = spacing,
    )
}

/**
 * A color picker driven by a plain [color] and [onColorChange]; the picker shows [color] and reports
 * each change, which you store and pass back. Everything else matches the overload that takes a
 * [ComposeColorPickerState], see there.
 *
 * ```
 * var color by remember { mutableStateOf(Color(0xFF3D5AFE)) }
 * ComposeColorPicker(color = color, onColorChange = { color = it })
 * ```
 *
 * It remembers the hue of the last color it reported, so dragging to a gray or black and back keeps it.
 */
@Composable
public fun ComposeColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onColorChangeFinished: ((Color) -> Unit)? = null,
    style: ColorPickerStyle = ColorPickerStyle.Wheel,
    enabled: Boolean = true,
    showAlpha: Boolean = true,
    showPreview: Boolean = true,
    showHexField: Boolean = true,
    originalColor: Color? = null,
    swatches: List<Color> = ComposeColorPickerDefaults.Swatches,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    sliderHeight: Dp = ComposeColorPickerDefaults.SliderHeight,
    ringWidth: Dp = ComposeColorPickerDefaults.RingWidth,
    panelShape: Shape = ComposeColorPickerDefaults.PanelShape,
    swatchSize: Dp = ComposeColorPickerDefaults.SwatchSize,
    spacing: Dp = ComposeColorPickerDefaults.Spacing,
) {
    // The last HSV this picker reported. While [color] is still that color we keep using it, so a gray
    // keeps its hue; any other color (set from outside, or a change the caller rejected) wins.
    var reported by remember { mutableStateOf(HsvColor.from(color)) }
    val hsvColor = if (reported.toArgb() == color.toArgb()) reported else HsvColor.from(color, previous = reported)

    ColorPickerContent(
        hsvColor = hsvColor,
        onHsvColorChange = {
            reported = it
            onColorChange(it.toColor())
        },
        onChangeFinished = { onColorChangeFinished?.invoke(reported.toColor()) },
        modifier = modifier,
        style = style,
        enabled = enabled,
        showAlpha = showAlpha,
        showPreview = showPreview,
        showHexField = showHexField,
        originalColor = originalColor,
        swatches = swatches,
        colors = colors,
        thumbSize = thumbSize,
        sliderHeight = sliderHeight,
        ringWidth = ringWidth,
        panelShape = panelShape,
        swatchSize = swatchSize,
        spacing = spacing,
    )
}

@Composable
private fun ColorPickerContent(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    onChangeFinished: () -> Unit,
    modifier: Modifier,
    style: ColorPickerStyle,
    enabled: Boolean,
    showAlpha: Boolean,
    showPreview: Boolean,
    showHexField: Boolean,
    originalColor: Color?,
    swatches: List<Color>,
    colors: ComposeColorPickerColors,
    thumbSize: Dp,
    sliderHeight: Dp,
    ringWidth: Dp,
    panelShape: Shape,
    swatchSize: Dp,
    spacing: Dp,
) {
    // Swatches and the original color set a whole color; a gray keeps the current hue.
    fun pick(color: Color) {
        onHsvColorChange(HsvColor.from(color, previous = hsvColor))
        onChangeFinished()
    }

    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (style) {
            ColorPickerStyle.Wheel -> {
                HueSaturationWheel(
                    hsvColor = hsvColor,
                    onHsvColorChange = onHsvColorChange,
                    enabled = enabled,
                    colors = colors,
                    thumbSize = thumbSize,
                    onColorChangeFinished = onChangeFinished,
                )
                BrightnessSlider(
                    hsvColor = hsvColor,
                    onHsvColorChange = onHsvColorChange,
                    enabled = enabled,
                    colors = colors,
                    trackHeight = sliderHeight,
                    thumbSize = thumbSize,
                    onColorChangeFinished = onChangeFinished,
                )
            }
            ColorPickerStyle.Ring -> HueRingPanel(
                hsvColor = hsvColor,
                onHsvColorChange = onHsvColorChange,
                enabled = enabled,
                colors = colors,
                ringWidth = ringWidth,
                shape = panelShape,
                thumbSize = thumbSize,
                onColorChangeFinished = onChangeFinished,
            )
            ColorPickerStyle.Square -> {
                SaturationValuePanel(
                    hsvColor = hsvColor,
                    onHsvColorChange = onHsvColorChange,
                    enabled = enabled,
                    colors = colors,
                    shape = panelShape,
                    thumbSize = thumbSize,
                    onColorChangeFinished = onChangeFinished,
                )
                HueSlider(
                    hsvColor = hsvColor,
                    onHsvColorChange = onHsvColorChange,
                    enabled = enabled,
                    colors = colors,
                    trackHeight = sliderHeight,
                    thumbSize = thumbSize,
                    onColorChangeFinished = onChangeFinished,
                )
            }
        }

        if (showAlpha) {
            AlphaSlider(
                hsvColor = hsvColor,
                onHsvColorChange = onHsvColorChange,
                enabled = enabled,
                colors = colors,
                trackHeight = sliderHeight,
                thumbSize = thumbSize,
                onColorChangeFinished = onChangeFinished,
            )
        }

        if (showPreview || showHexField) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showPreview) {
                    ColorPreview(
                        color = hsvColor.toColor(),
                        originalColor = originalColor,
                        onOriginalClick = if (enabled && originalColor != null) ({ pick(originalColor) }) else null,
                        colors = colors,
                    )
                }
                if (showHexField) {
                    ColorHexField(
                        hsvColor = hsvColor,
                        onHsvColorChange = onHsvColorChange,
                        modifier = Modifier.weight(1f),
                        enabled = enabled,
                        showAlpha = showAlpha,
                        colors = colors,
                        onColorChangeFinished = onChangeFinished,
                    )
                }
            }
        }

        if (swatches.isNotEmpty()) {
            ColorSwatches(
                swatches = swatches,
                selectedColor = hsvColor.toColor(),
                onSwatchClick = ::pick,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                colors = colors,
                swatchSize = swatchSize,
            )
        }
    }
}
