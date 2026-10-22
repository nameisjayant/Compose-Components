package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/** Pure hues every 60° from red round to red, for sweep and slider gradients. */
internal val HueStops: List<Color> = List(7) { Color(hsvToArgb(it * 60f, 1f, 1f, 1f)) }

internal val TransparentWhite: Color = Color.White.copy(alpha = 0f)
private val TransparentBlack = Color.Black.copy(alpha = 0f)

/**
 * Tracks one finger from touch-down until it lifts, reporting each position. The gesture is claimed on
 * touch-down so a press lands the thumb straight away. Does nothing when [enabled] is false.
 *
 * The gesture outlives recompositions, so callers read changing values through `rememberUpdatedState`.
 */
internal fun Modifier.colorDrag(
    enabled: Boolean,
    onDown: PointerInputScope.(Offset) -> Unit = {},
    onFinished: () -> Unit,
    onPosition: PointerInputScope.(Offset) -> Unit,
): Modifier = if (!enabled) this else pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown()
        down.consume()
        onDown(down.position)
        onPosition(down.position)
        drag(down.id) { change ->
            onPosition(change.position)
            change.consume()
        }
        onFinished()
    }
}

/** Fills the current clip with a checkerboard, so see-through colors read as see-through. */
internal fun DrawScope.drawCheckerboard(light: Color, dark: Color) {
    val cell = 6.dp.toPx()
    drawRect(light)
    val columns = ceil(size.width / cell).toInt()
    val rows = ceil(size.height / cell).toInt()
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            if ((row + column) % 2 == 1) drawRect(dark, Offset(column * cell, row * cell), Size(cell, cell))
        }
    }
}

/** A round thumb: a [ComposeColorPickerColors.thumbColor] ring around [fill], with a thin edge. */
internal fun DrawScope.drawColorThumb(center: Offset, radius: Float, fill: Color, colors: ComposeColorPickerColors) {
    val edge = 1.dp.toPx()
    drawCircle(colors.thumbBorderColor, radius + edge, center)
    drawCircle(colors.thumbColor, radius, center)
    drawCircle(fill, radius - 3.dp.toPx(), center)
    drawCircle(colors.thumbBorderColor, radius - 3.dp.toPx(), center, style = Stroke(edge / 2f))
}

/**
 * Draws a saturation (left to right) by brightness (top to bottom) surface of [hue] over [surface],
 * clipped to [shape]. The gradients span [mapping], which can sit inside [surface] so the thumb never
 * has to leave it; outside [mapping] the edge colors carry on.
 */
internal fun DrawScope.drawSaturationValue(
    hue: Float,
    surface: Rect,
    mapping: Rect,
    shape: Shape,
    borderColor: Color,
) {
    translate(surface.left, surface.top) {
        val outline = shape.createOutline(surface.size, layoutDirection, this)
        val clip = Path().apply { addOutline(outline) }
        clipPath(clip) {
            drawRect(Color(hsvToArgb(hue, 1f, 1f, 1f)), size = surface.size)
            drawRect(
                Brush.horizontalGradient(
                    listOf(Color.White, TransparentWhite),
                    startX = mapping.left - surface.left,
                    endX = mapping.right - surface.left,
                ),
                size = surface.size,
            )
            drawRect(
                Brush.verticalGradient(
                    listOf(TransparentBlack, Color.Black),
                    startY = mapping.top - surface.top,
                    endY = mapping.bottom - surface.top,
                ),
                size = surface.size,
            )
        }
        drawPath(clip, borderColor, style = Stroke(1.dp.toPx()))
    }
}

/** Where the saturation/value thumb of [color] sits in [mapping]. */
internal fun saturationValueOffset(color: HsvColor, mapping: Rect): Offset = Offset(
    mapping.left + color.saturation * mapping.width,
    mapping.top + (1f - color.value) * mapping.height,
)

/** The saturation and value at [position] in [mapping], clamped to its edges. */
internal fun HsvColor.withSaturationValueAt(position: Offset, mapping: Rect): HsvColor = copy(
    saturation = if (mapping.width > 0f) (position.x - mapping.left) / mapping.width else saturation,
    value = if (mapping.height > 0f) 1f - (position.y - mapping.top) / mapping.height else value,
)

/** Fully opaque copy, for thumbs that show the color without its alpha. */
internal fun HsvColor.opaque(): Color = copy(alpha = 1f).toColor()
