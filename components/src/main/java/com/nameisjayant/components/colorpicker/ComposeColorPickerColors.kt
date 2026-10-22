package com.nameisjayant.components.colorpicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeColorPicker] and its parts. These dress the picker, not the colors it picks:
 * thumbs are a [thumbColor] ring with a [thumbBorderColor] edge, panels and sliders get a [borderColor]
 * outline, and see-through colors sit on a checkerboard of [checkerboardLightColor] and
 * [checkerboardDarkColor]. The hex field uses the `field*` colors.
 *
 * Get the theme-based defaults from [ComposeColorPickerDefaults.colors], a palette around one color
 * from [ComposeColorPickerDefaults.accentColors], or tweak any of them with [copy].
 */
@Immutable
public class ComposeColorPickerColors(
    public val thumbColor: Color,
    public val thumbBorderColor: Color,
    public val borderColor: Color,
    public val checkerboardLightColor: Color,
    public val checkerboardDarkColor: Color,
    public val selectedSwatchBorderColor: Color,
    public val fieldContainerColor: Color,
    public val fieldTextColor: Color,
    public val fieldLabelColor: Color,
    public val fieldBorderColor: Color,
    public val fieldFocusedBorderColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        thumbColor: Color = Color.Unspecified,
        thumbBorderColor: Color = Color.Unspecified,
        borderColor: Color = Color.Unspecified,
        checkerboardLightColor: Color = Color.Unspecified,
        checkerboardDarkColor: Color = Color.Unspecified,
        selectedSwatchBorderColor: Color = Color.Unspecified,
        fieldContainerColor: Color = Color.Unspecified,
        fieldTextColor: Color = Color.Unspecified,
        fieldLabelColor: Color = Color.Unspecified,
        fieldBorderColor: Color = Color.Unspecified,
        fieldFocusedBorderColor: Color = Color.Unspecified,
    ): ComposeColorPickerColors = ComposeColorPickerColors(
        thumbColor = thumbColor.or(this.thumbColor),
        thumbBorderColor = thumbBorderColor.or(this.thumbBorderColor),
        borderColor = borderColor.or(this.borderColor),
        checkerboardLightColor = checkerboardLightColor.or(this.checkerboardLightColor),
        checkerboardDarkColor = checkerboardDarkColor.or(this.checkerboardDarkColor),
        selectedSwatchBorderColor = selectedSwatchBorderColor.or(this.selectedSwatchBorderColor),
        fieldContainerColor = fieldContainerColor.or(this.fieldContainerColor),
        fieldTextColor = fieldTextColor.or(this.fieldTextColor),
        fieldLabelColor = fieldLabelColor.or(this.fieldLabelColor),
        fieldBorderColor = fieldBorderColor.or(this.fieldBorderColor),
        fieldFocusedBorderColor = fieldFocusedBorderColor.or(this.fieldFocusedBorderColor),
    )

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        thumbColor, thumbBorderColor, borderColor, checkerboardLightColor, checkerboardDarkColor,
        selectedSwatchBorderColor, fieldContainerColor, fieldTextColor, fieldLabelColor, fieldBorderColor,
        fieldFocusedBorderColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeColorPickerColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
