package com.nameisjayant.components.timeline

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [ComposeTimeline]. */
public object ComposeTimelineDefaults {

    /** Size of the default indicators, and the width of the track the line runs along. */
    public val IndicatorSize: Dp = 24.dp

    /** Gap between one step and the next; the line runs through it. */
    public val ItemSpacing: Dp = 24.dp

    /** Gap between the line and the content on either side of it. */
    public val ContentSpacing: Dp = 16.dp

    /** Share of the space beside the line that opposite content gets when there is some. */
    public const val OppositeContentFraction: Float = 0.3f

    /** Colors from [MaterialTheme.colorScheme]; any of them can be overridden. */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        completedColor: Color = MaterialTheme.colorScheme.primary,
        onCompletedColor: Color = MaterialTheme.colorScheme.onPrimary,
        activeColor: Color = completedColor,
        onActiveColor: Color = onCompletedColor,
        upcomingColor: Color = MaterialTheme.colorScheme.outline,
        errorColor: Color = MaterialTheme.colorScheme.error,
        onErrorColor: Color = MaterialTheme.colorScheme.onError,
        containerColor: Color = Color.Transparent,
        completedLineColor: Color = completedColor,
        upcomingLineColor: Color = MaterialTheme.colorScheme.outlineVariant,
        haloColor: Color = activeColor,
    ): ComposeTimelineColors = ComposeTimelineColors(
        completedColor = completedColor,
        onCompletedColor = onCompletedColor,
        activeColor = activeColor,
        onActiveColor = onActiveColor,
        upcomingColor = upcomingColor,
        errorColor = errorColor,
        onErrorColor = onErrorColor,
        containerColor = containerColor,
        completedLineColor = completedLineColor,
        upcomingLineColor = upcomingLineColor,
        haloColor = haloColor,
    )

    /**
     * Completed and active steps and the reached line in [accent]; upcoming steps and errors follow
     * [MaterialTheme]. [onAccent] defaults to black or white, whichever reads better on [accent].
     */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(
        accent: Color,
        onAccent: Color = if (accent.luminance() > 0.5f) Color.Black else Color.White,
    ): ComposeTimelineColors = colors(completedColor = accent, onCompletedColor = onAccent)
}
