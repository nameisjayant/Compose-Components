package com.nameisjayant.components.wheelpicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeWheelPicker]. Row text blends from [textColor] to [selectedTextColor] as it
 * reaches the selection line; items that can't be picked use [disabledItemTextColor], and a disabled
 * picker draws every row in [disabledTextColor].
 *
 * Get the theme-based defaults from [ComposeWheelPickerDefaults.colors], a palette around one color
 * from [ComposeWheelPickerDefaults.accentColors], or tweak any of them with [copy].
 */
@Immutable
public class ComposeWheelPickerColors(
    public val textColor: Color,
    public val selectedTextColor: Color,
    public val disabledItemTextColor: Color,
    public val disabledTextColor: Color,
    public val indicatorColor: Color,
    public val indicatorLineColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        textColor: Color = Color.Unspecified,
        selectedTextColor: Color = Color.Unspecified,
        disabledItemTextColor: Color = Color.Unspecified,
        disabledTextColor: Color = Color.Unspecified,
        indicatorColor: Color = Color.Unspecified,
        indicatorLineColor: Color = Color.Unspecified,
    ): ComposeWheelPickerColors = ComposeWheelPickerColors(
        textColor = textColor.or(this.textColor),
        selectedTextColor = selectedTextColor.or(this.selectedTextColor),
        disabledItemTextColor = disabledItemTextColor.or(this.disabledItemTextColor),
        disabledTextColor = disabledTextColor.or(this.disabledTextColor),
        indicatorColor = indicatorColor.or(this.indicatorColor),
        indicatorLineColor = indicatorLineColor.or(this.indicatorLineColor),
    )

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        textColor, selectedTextColor, disabledItemTextColor, disabledTextColor, indicatorColor, indicatorLineColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeWheelPickerColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
