package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A disc of every hue round the edge, fading to white in the middle: angle picks [HsvColor.hue] and
 * distance from the center picks [HsvColor.saturation]. It shows the current brightness, so pair it
 * with a [BrightnessSlider]. Stateless, like [HueSlider].
 *
 * It is square and fills the width it is given unless [modifier] sizes it.
 */
@Composable
public fun HueSaturationWheel(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val current by rememberUpdatedState(hsvColor)
    val onChange by rememberUpdatedState(onHsvColorChange)
    val onFinished by rememberUpdatedState(onColorChangeFinished)

    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha)
            .panelSemantics(hsvColor, enabled)
            .colorDrag(enabled, onFinished = { onFinished?.invoke() }) { position ->
                val radius = wheelRadius(Size(size.width.toFloat(), size.height.toFloat()), thumbSize)
                if (radius <= 0f) return@colorDrag
                val dx = position.x - size.width / 2f
                val dy = position.y - size.height / 2f
                onChange(current.copy(hue = angleOf(dx, dy), saturation = hypot(dx, dy) / radius))
            },
    ) {
        val radius = wheelRadius(size, thumbSize)
        if (radius <= 0f) return@Canvas
        drawCircle(Brush.sweepGradient(HueStops, center), radius)
        drawCircle(Brush.radialGradient(listOf(Color.White, TransparentWhite), center, radius), radius)
        if (hsvColor.value < 1f) drawCircle(Color.Black.copy(alpha = 1f - hsvColor.value), radius)
        drawCircle(colors.borderColor, radius, style = Stroke(1.dp.toPx()))

        val angle = Math.toRadians(hsvColor.hue.toDouble())
        val distance = radius * hsvColor.saturation
        drawColorThumb(
            center = center + Offset((cos(angle) * distance).toFloat(), (sin(angle) * distance).toFloat()),
            radius = thumbSize.toPx() / 2f,
            fill = hsvColor.opaque(),
            colors = colors,
        )
    }
}

/**
 * A panel of one hue: [HsvColor.saturation] grows left to right and [HsvColor.value] (brightness) top
 * to bottom. Pair it with a [HueSlider]. Stateless, like [HueSlider].
 *
 * It fills the width it is given at [aspectRatio] (width over height) unless [modifier] sizes it.
 */
@Composable
public fun SaturationValuePanel(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    shape: Shape = ComposeColorPickerDefaults.PanelShape,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    aspectRatio: Float = ComposeColorPickerDefaults.PanelAspectRatio,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    require(aspectRatio > 0f) { "aspectRatio must be positive, was $aspectRatio" }
    val current by rememberUpdatedState(hsvColor)
    val onChange by rememberUpdatedState(onHsvColorChange)
    val onFinished by rememberUpdatedState(onColorChangeFinished)

    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha)
            .panelSemantics(hsvColor, enabled)
            .colorDrag(enabled, onFinished = { onFinished?.invoke() }) { position ->
                val mapping = panelMapping(Size(size.width.toFloat(), size.height.toFloat()), thumbSize)
                onChange(current.withSaturationValueAt(position, mapping))
            },
    ) {
        val mapping = panelMapping(size, thumbSize)
        drawSaturationValue(hsvColor.hue, Rect(Offset.Zero, size), mapping, shape, colors.borderColor)
        drawColorThumb(saturationValueOffset(hsvColor, mapping), thumbSize.toPx() / 2f, hsvColor.opaque(), colors)
    }
}

/**
 * A ring of every hue around a saturation/brightness square: drag on the ring to pick
 * [HsvColor.hue], inside it to pick [HsvColor.saturation] and [HsvColor.value]. A drag stays with
 * whichever part it started on. Stateless, like [HueSlider].
 *
 * It is square and fills the width it is given unless [modifier] sizes it.
 *
 * @param ringWidth thickness of the hue ring.
 * @param shape corners of the inner square.
 */
@Composable
public fun HueRingPanel(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    ringWidth: Dp = ComposeColorPickerDefaults.RingWidth,
    shape: Shape = ComposeColorPickerDefaults.PanelShape,
    thumbSize: Dp = ComposeColorPickerDefaults.ThumbSize,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    require(ringWidth > 0.dp) { "ringWidth must be positive, was $ringWidth" }
    val current by rememberUpdatedState(hsvColor)
    val onChange by rememberUpdatedState(onHsvColorChange)
    val onFinished by rememberUpdatedState(onColorChangeFinished)
    // Which part the current drag started on; it keeps that part until the finger lifts.
    var draggingRing by remember { mutableStateOf(false) }

    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha)
            .panelSemantics(hsvColor, enabled)
            .colorDrag(
                enabled = enabled,
                onDown = { position ->
                    val geometry = ringGeometry(Size(size.width.toFloat(), size.height.toFloat()), ringWidth, thumbSize)
                    val distance = hypot(position.x - size.width / 2f, position.y - size.height / 2f)
                    draggingRing = distance >= geometry.innerRadius + geometry.gap / 2f
                },
                onFinished = { onFinished?.invoke() },
            ) { position ->
                val geometry = ringGeometry(Size(size.width.toFloat(), size.height.toFloat()), ringWidth, thumbSize)
                if (draggingRing) {
                    onChange(current.copy(hue = angleOf(position.x - size.width / 2f, position.y - size.height / 2f)))
                } else {
                    onChange(current.withSaturationValueAt(position, geometry.square))
                }
            },
    ) {
        val geometry = ringGeometry(size, ringWidth, thumbSize)
        if (geometry.ringRadius <= 0f) return@Canvas
        val ringPx = ringWidth.toPx()
        drawCircle(Brush.sweepGradient(HueStops, center), geometry.ringRadius, style = Stroke(ringPx))
        val hairline = Stroke(1.dp.toPx())
        drawCircle(colors.borderColor, geometry.ringRadius + ringPx / 2f, style = hairline)
        drawCircle(colors.borderColor, geometry.ringRadius - ringPx / 2f, style = hairline)

        if (geometry.square.width > 0f) {
            drawSaturationValue(hsvColor.hue, geometry.square, geometry.square, shape, colors.borderColor)
        }

        val thumbRadius = thumbSize.toPx() / 2f
        val angle = Math.toRadians(hsvColor.hue.toDouble())
        drawColorThumb(
            center = center + Offset(
                (cos(angle) * geometry.ringRadius).toFloat(),
                (sin(angle) * geometry.ringRadius).toFloat(),
            ),
            radius = thumbRadius,
            fill = Color(hsvToArgb(hsvColor.hue, 1f, 1f, 1f)),
            colors = colors,
        )
        if (geometry.square.width > 0f) {
            drawColorThumb(saturationValueOffset(hsvColor, geometry.square), thumbRadius, hsvColor.opaque(), colors)
        }
    }
}

/** Reads a 2D panel to screen readers as "Color", with the hex code; the hex field and sliders edit it. */
@Composable
private fun Modifier.panelSemantics(hsvColor: HsvColor, enabled: Boolean): Modifier {
    val label = stringResource(R.string.cccp_color)
    val hex = "#" + formatHex(hsvColor.toArgb(), includeAlpha = hsvColor.alpha < 1f)
    return semantics {
        contentDescription = label
        stateDescription = hex
        if (!enabled) disabled()
    }
}

/** Hue in degrees (0..360, clockwise from 3 o'clock) of a point [dx], [dy] from the center. */
private fun angleOf(dx: Float, dy: Float): Float =
    ((Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()) + 360f) % 360f

/** Radius of the wheel, leaving room for the thumb at its edge. */
private fun Density.wheelRadius(size: Size, thumbSize: Dp): Float =
    min(size.width, size.height) / 2f - thumbSize.toPx() / 2f

/** The area a panel's gradients span: inset by the thumb radius so the thumb never leaves the panel. */
private fun Density.panelMapping(size: Size, thumbSize: Dp): Rect {
    val inset = (thumbSize.toPx() / 2f).coerceAtMost(min(size.width, size.height) / 2f)
    return Rect(inset, inset, size.width - inset, size.height - inset)
}

private class RingGeometry(val ringRadius: Float, val innerRadius: Float, val gap: Float, val square: Rect)

/** Where the hue ring runs and where the square inside it sits, for a panel of [size]. */
private fun Density.ringGeometry(size: Size, ringWidth: Dp, thumbSize: Dp): RingGeometry {
    val ringPx = ringWidth.toPx()
    val thumbOverhang = (thumbSize.toPx() - ringPx).coerceAtLeast(0f) / 2f
    val outer = min(size.width, size.height) / 2f - thumbOverhang
    val ringRadius = outer - ringPx / 2f
    val gap = 12.dp.toPx()
    val innerRadius = (outer - ringPx - gap).coerceAtLeast(0f)
    val half = innerRadius / sqrt(2f)
    val center = Offset(size.width / 2f, size.height / 2f)
    return RingGeometry(ringRadius, innerRadius, gap, Rect(center - Offset(half, half), center + Offset(half, half)))
}
