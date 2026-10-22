package com.nameisjayant.components.colorpicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * A color as hue, saturation, value (brightness) and alpha: the model [ComposeColorPicker] works in.
 *
 * Unlike a [Color], it remembers the hue of a gray and the saturation of black, so a picker can drag
 * brightness to zero and back without losing the hue. Out-of-range values are clamped.
 *
 * @param hue in degrees, 0..360 (both ends are red); other values wrap round.
 * @param saturation 0 (gray) .. 1 (full color).
 * @param value brightness, 0 (black) .. 1.
 * @param alpha 0 (transparent) .. 1 (opaque).
 */
@Immutable
public class HsvColor(hue: Float, saturation: Float, value: Float, alpha: Float = 1f) {

    public val hue: Float = when {
        hue.isNaN() -> 0f
        hue in 0f..360f -> hue
        else -> ((hue % 360f) + 360f) % 360f
    }
    public val saturation: Float = saturation.unit()
    public val value: Float = value.unit()
    public val alpha: Float = alpha.unit()

    /** This color as an sRGB [Color], with 8 bits per channel. */
    public fun toColor(): Color = Color(toArgb())

    /** This color packed as 0xAARRGGBB. */
    public fun toArgb(): Int = hsvToArgb(hue, saturation, value, alpha)

    public fun copy(
        hue: Float = this.hue,
        saturation: Float = this.saturation,
        value: Float = this.value,
        alpha: Float = this.alpha,
    ): HsvColor = HsvColor(hue, saturation, value, alpha)

    override fun equals(other: Any?): Boolean =
        this === other || (other is HsvColor && hue == other.hue && saturation == other.saturation &&
            value == other.value && alpha == other.alpha)

    override fun hashCode(): Int {
        var result = hue.hashCode()
        result = 31 * result + saturation.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + alpha.hashCode()
        return result
    }

    override fun toString(): String = "HsvColor(hue=$hue, saturation=$saturation, value=$value, alpha=$alpha)"

    public companion object {
        /**
         * The [HsvColor] for [color]. A gray (or black) has no hue of its own, so it takes the hue (and
         * saturation) of [previous] when given.
         */
        public fun from(color: Color, previous: HsvColor? = null): HsvColor = hsvFromArgb(color.toArgb(), previous)
    }
}

private fun Float.unit(): Float = if (isNaN()) 0f else coerceIn(0f, 1f)
