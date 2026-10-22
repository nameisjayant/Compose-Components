package com.nameisjayant.components.wheelpicker

import androidx.compose.runtime.Immutable

/**
 * How the rows of a [ComposeWheelPicker] look away from the selection line.
 *
 * A [curved] wheel bends its rows round a cylinder, like a physical drum, so rows near the edges tilt
 * away and bunch up. Either way rows fade towards [outerAlpha] and shrink towards [outerScale] as they
 * get further from the center.
 *
 * Use [Wheel] or [Flat], or build your own.
 *
 * @property curved bend the rows round a cylinder.
 * @property outerAlpha opacity of rows at the edge of the wheel; the center row is fully opaque.
 * @property outerScale size of rows at the edge of the wheel; the center row is full size.
 */
@Immutable
public class WheelPickerStyle(
    public val curved: Boolean = true,
    public val outerAlpha: Float = 0.3f,
    public val outerScale: Float = 1f,
) {

    init {
        require(outerAlpha in 0f..1f) { "outerAlpha must be in 0..1, was $outerAlpha" }
        require(outerScale > 0f) { "outerScale must be positive, was $outerScale" }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is WheelPickerStyle &&
                curved == other.curved &&
                outerAlpha == other.outerAlpha &&
                outerScale == other.outerScale
            )

    override fun hashCode(): Int {
        var result = curved.hashCode()
        result = 31 * result + outerAlpha.hashCode()
        result = 31 * result + outerScale.hashCode()
        return result
    }

    public companion object {
        /** Rows curve round a drum, like an iOS picker. */
        public val Wheel: WheelPickerStyle = WheelPickerStyle()

        /** Rows stay flat and shrink a little towards the edges. */
        public val Flat: WheelPickerStyle = WheelPickerStyle(curved = false, outerAlpha = 0.35f, outerScale = 0.82f)
    }
}
