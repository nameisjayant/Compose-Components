package com.nameisjayant.components.ratingbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [ComposeRatingBar]. */
public object ComposeRatingBarDefaults {

    public val ItemSize: Dp = 32.dp
    public val ItemSpacing: Dp = 6.dp

    /** A good border width for [outlinedColors]. */
    public val OutlineWidth: Dp = 1.5.dp

    /** The classic rating gold, for [accentColors]. */
    public val Gold: Color = Color(0xFFFFB400)

    /**
     * A star with [points] tips.
     *
     * @param innerRadiusRatio how deep the notches between tips go (smaller = spikier).
     * @param roundness 0 for sharp corners up to 1 for a soft, blobby star.
     */
    public fun starShape(points: Int = 5, innerRadiusRatio: Float = 0.47f, roundness: Float = 0f): Shape =
        PolygonStarShape(points, innerRadiusRatio, roundness)

    /** Five-point star with softly rounded tips (the default). */
    public val StarShape: Shape = starShape(roundness = 0.28f)

    /** Five-point star with crisp tips. */
    public val SharpStarShape: Shape = starShape(innerRadiusRatio = 0.42f)

    public val HeartShape: Shape = com.nameisjayant.components.ratingbar.HeartShape

    /** Colors from [MaterialTheme.colorScheme]; any of them can be overridden. */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        activeColor: Color = MaterialTheme.colorScheme.primary,
        inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        activeBorderColor: Color = activeColor,
        inactiveBorderColor: Color = MaterialTheme.colorScheme.outline,
        disabledActiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledInactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        haloColor: Color = activeColor,
    ): ComposeRatingBarColors = ComposeRatingBarColors(
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        activeBorderColor = activeBorderColor,
        inactiveBorderColor = inactiveBorderColor,
        disabledActiveColor = disabledActiveColor,
        disabledInactiveColor = disabledInactiveColor,
        haloColor = haloColor,
    )

    /** Filled items, border and halo in [accent]; empty items follow [MaterialTheme]. */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(
        accent: Color = Gold,
        inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ): ComposeRatingBarColors = colors(activeColor = accent, inactiveColor = inactiveColor)

    /**
     * Empty items are drawn as an outline only. Pair with `borderWidth = OutlineWidth` on the bar.
     */
    @Composable
    @ReadOnlyComposable
    public fun outlinedColors(
        accent: Color = MaterialTheme.colorScheme.primary,
        inactiveBorderColor: Color = MaterialTheme.colorScheme.outline,
    ): ComposeRatingBarColors = colors(
        activeColor = accent,
        inactiveColor = Color.Transparent,
        inactiveBorderColor = inactiveBorderColor,
        disabledInactiveColor = Color.Transparent,
    )
}
