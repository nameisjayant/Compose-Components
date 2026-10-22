package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [ComposeColorPicker] and its parts. */
public object ComposeColorPickerDefaults {

    /** Diameter of the round thumbs on the panels and sliders. */
    public val ThumbSize: Dp = 28.dp

    /** Thickness of the slider tracks. */
    public val SliderHeight: Dp = 24.dp

    /** Thickness of the [HueRingPanel] ring. */
    public val RingWidth: Dp = 28.dp

    public val SwatchSize: Dp = 36.dp
    public val SwatchSpacing: Dp = 10.dp

    /** Gap between the picker's sections. */
    public val Spacing: Dp = 16.dp

    /** Height of the preview and the hex field. */
    public val FieldHeight: Dp = 44.dp
    public val PreviewWidth: Dp = 64.dp

    /** Width over height of the [ColorPickerStyle.Square] panel. */
    public const val PanelAspectRatio: Float = 1.5f

    /** Alpha of a disabled picker. */
    public const val DisabledAlpha: Float = 0.38f

    public val PanelShape: Shape = RoundedCornerShape(16.dp)
    public val PreviewShape: Shape = RoundedCornerShape(12.dp)
    public val FieldShape: Shape = RoundedCornerShape(12.dp)
    public val SwatchShape: Shape = CircleShape

    /** A spread of everyday colors, plus white and black, for the swatch row. */
    public val Swatches: List<Color> = listOf(
        Color(0xFFF44336), Color(0xFFFF9800), Color(0xFFFFEB3B), Color(0xFF8BC34A),
        Color(0xFF4CAF50), Color(0xFF009688), Color(0xFF00BCD4), Color(0xFF2196F3),
        Color(0xFF3F51B5), Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFF795548),
        Color(0xFF9E9E9E), Color(0xFF607D8B), Color(0xFFFFFFFF), Color(0xFF000000),
    )

    /** Colors from [MaterialTheme.colorScheme]; any of them can be overridden. */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        thumbColor: Color = Color.White,
        thumbBorderColor: Color = Color.Black.copy(alpha = 0.25f),
        borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
        checkerboardLightColor: Color = Color(0xFFFFFFFF),
        checkerboardDarkColor: Color = Color(0xFFD6D6D6),
        selectedSwatchBorderColor: Color = MaterialTheme.colorScheme.onSurface,
        fieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        fieldTextColor: Color = MaterialTheme.colorScheme.onSurface,
        fieldLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        fieldBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
        fieldFocusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    ): ComposeColorPickerColors = ComposeColorPickerColors(
        thumbColor = thumbColor,
        thumbBorderColor = thumbBorderColor,
        borderColor = borderColor,
        checkerboardLightColor = checkerboardLightColor,
        checkerboardDarkColor = checkerboardDarkColor,
        selectedSwatchBorderColor = selectedSwatchBorderColor,
        fieldContainerColor = fieldContainerColor,
        fieldTextColor = fieldTextColor,
        fieldLabelColor = fieldLabelColor,
        fieldBorderColor = fieldBorderColor,
        fieldFocusedBorderColor = fieldFocusedBorderColor,
    )

    /** Selected swatch ring and focused hex field in [accent]; everything else follows [MaterialTheme]. */
    @Composable
    @ReadOnlyComposable
    public fun accentColors(accent: Color): ComposeColorPickerColors = colors(
        selectedSwatchBorderColor = accent,
        fieldFocusedBorderColor = accent,
    )
}
