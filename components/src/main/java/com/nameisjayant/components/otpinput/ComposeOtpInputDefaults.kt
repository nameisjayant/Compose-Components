package com.nameisjayant.components.otpinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Defaults for [ComposeOtpInput]. */
public object ComposeOtpInputDefaults {

    public val CellSize: DpSize = DpSize(48.dp, 56.dp)
    public val CellSpacing: Dp = 8.dp
    public val CellShape: Shape = RoundedCornerShape(12.dp)

    /** Round cells; pair with a square [CellSize] such as `DpSize(52.dp, 52.dp)`. */
    public val CircleCellShape: Shape = CircleShape

    public val BorderWidth: Dp = 1.dp
    public val FocusedBorderWidth: Dp = 2.dp

    /** Shown instead of each character when the input is masked. */
    public const val MaskChar: Char = '●'

    /** How long a masked input shows the character just typed before hiding it. */
    public const val RevealMillis: Long = 600L

    /** Large, centered digits from [MaterialTheme.typography]. */
    @Composable
    @ReadOnlyComposable
    public fun textStyle(): TextStyle =
        MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)

    /**
     * Colors from [MaterialTheme.colorScheme] that suit [style]; any of them can be overridden.
     * Filled cells get a tinted container, the other styles a transparent one.
     */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        style: OtpCellStyle = OtpCellStyle.Outlined,
        containerColor: Color = if (style == OtpCellStyle.Filled) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
        focusedContainerColor: Color = containerColor,
        filledContainerColor: Color = if (style == OtpCellStyle.Filled) MaterialTheme.colorScheme.surfaceContainerHighest else containerColor,
        errorContainerColor: Color = if (style == OtpCellStyle.Filled) MaterialTheme.colorScheme.errorContainer else containerColor,
        disabledContainerColor: Color = if (style == OtpCellStyle.Filled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f) else Color.Transparent,
        borderColor: Color = if (style == OtpCellStyle.Filled) Color.Transparent else MaterialTheme.colorScheme.outline,
        focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
        filledBorderColor: Color = if (style == OtpCellStyle.Filled) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant,
        errorBorderColor: Color = MaterialTheme.colorScheme.error,
        disabledBorderColor: Color = if (style == OtpCellStyle.Filled) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        textColor: Color = MaterialTheme.colorScheme.onSurface,
        errorTextColor: Color = MaterialTheme.colorScheme.error,
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        placeholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        cursorColor: Color = focusedBorderColor,
    ): ComposeOtpInputColors = ComposeOtpInputColors(
        containerColor = containerColor,
        focusedContainerColor = focusedContainerColor,
        filledContainerColor = filledContainerColor,
        errorContainerColor = errorContainerColor,
        disabledContainerColor = disabledContainerColor,
        borderColor = borderColor,
        focusedBorderColor = focusedBorderColor,
        filledBorderColor = filledBorderColor,
        errorBorderColor = errorBorderColor,
        disabledBorderColor = disabledBorderColor,
        textColor = textColor,
        errorTextColor = errorTextColor,
        disabledTextColor = disabledTextColor,
        placeholderColor = placeholderColor,
        cursorColor = cursorColor,
    )

    /** Focused border, cursor and filled cells in [accent]; everything else follows [MaterialTheme]. */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(
        accent: Color,
        style: OtpCellStyle = OtpCellStyle.Outlined,
    ): ComposeOtpInputColors = when (style) {
        OtpCellStyle.Filled -> colors(
            style = style,
            focusedBorderColor = accent,
            focusedContainerColor = accent.copy(alpha = 0.08f),
            filledContainerColor = accent.copy(alpha = 0.14f),
        )
        else -> colors(
            style = style,
            focusedBorderColor = accent,
            filledBorderColor = accent.copy(alpha = 0.6f),
        )
    }

    /** A short dash between groups of cells (see the input's `groupSize`). */
    @Composable
    public fun Separator(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.outline,
    ) {
        Box(
            modifier
                .size(width = 12.dp, height = 2.dp)
                .background(color, CircleShape),
        )
    }
}
