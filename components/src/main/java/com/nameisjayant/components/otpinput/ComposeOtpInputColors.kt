package com.nameisjayant.components.otpinput

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeOtpInput]. Each cell picks, in order of priority: disabled, error, focused
 * (the active cell while the input has focus), filled, then the plain empty colors.
 *
 * Get the theme-based defaults for a cell style from [ComposeOtpInputDefaults.colors], a palette
 * around one color from [ComposeOtpInputDefaults.accentColors], or tweak any of them with [copy].
 */
@Immutable
public class ComposeOtpInputColors(
    public val containerColor: Color,
    public val focusedContainerColor: Color,
    public val filledContainerColor: Color,
    public val errorContainerColor: Color,
    public val disabledContainerColor: Color,
    public val borderColor: Color,
    public val focusedBorderColor: Color,
    public val filledBorderColor: Color,
    public val errorBorderColor: Color,
    public val disabledBorderColor: Color,
    public val textColor: Color,
    public val errorTextColor: Color,
    public val disabledTextColor: Color,
    public val placeholderColor: Color,
    public val cursorColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        containerColor: Color = Color.Unspecified,
        focusedContainerColor: Color = Color.Unspecified,
        filledContainerColor: Color = Color.Unspecified,
        errorContainerColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        borderColor: Color = Color.Unspecified,
        focusedBorderColor: Color = Color.Unspecified,
        filledBorderColor: Color = Color.Unspecified,
        errorBorderColor: Color = Color.Unspecified,
        disabledBorderColor: Color = Color.Unspecified,
        textColor: Color = Color.Unspecified,
        errorTextColor: Color = Color.Unspecified,
        disabledTextColor: Color = Color.Unspecified,
        placeholderColor: Color = Color.Unspecified,
        cursorColor: Color = Color.Unspecified,
    ): ComposeOtpInputColors = ComposeOtpInputColors(
        containerColor = containerColor.or(this.containerColor),
        focusedContainerColor = focusedContainerColor.or(this.focusedContainerColor),
        filledContainerColor = filledContainerColor.or(this.filledContainerColor),
        errorContainerColor = errorContainerColor.or(this.errorContainerColor),
        disabledContainerColor = disabledContainerColor.or(this.disabledContainerColor),
        borderColor = borderColor.or(this.borderColor),
        focusedBorderColor = focusedBorderColor.or(this.focusedBorderColor),
        filledBorderColor = filledBorderColor.or(this.filledBorderColor),
        errorBorderColor = errorBorderColor.or(this.errorBorderColor),
        disabledBorderColor = disabledBorderColor.or(this.disabledBorderColor),
        textColor = textColor.or(this.textColor),
        errorTextColor = errorTextColor.or(this.errorTextColor),
        disabledTextColor = disabledTextColor.or(this.disabledTextColor),
        placeholderColor = placeholderColor.or(this.placeholderColor),
        cursorColor = cursorColor.or(this.cursorColor),
    )

    internal fun container(cell: OtpCell): Color = when {
        !cell.isEnabled -> disabledContainerColor
        cell.isError -> errorContainerColor
        cell.isActive -> focusedContainerColor
        cell.isFilled -> filledContainerColor
        else -> containerColor
    }

    internal fun border(cell: OtpCell): Color = when {
        !cell.isEnabled -> disabledBorderColor
        cell.isError -> errorBorderColor
        cell.isActive -> focusedBorderColor
        cell.isFilled -> filledBorderColor
        else -> borderColor
    }

    internal fun text(cell: OtpCell): Color = when {
        !cell.isEnabled -> disabledTextColor
        cell.isError -> errorTextColor
        else -> textColor
    }

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        containerColor, focusedContainerColor, filledContainerColor, errorContainerColor, disabledContainerColor,
        borderColor, focusedBorderColor, filledBorderColor, errorBorderColor, disabledBorderColor,
        textColor, errorTextColor, disabledTextColor, placeholderColor, cursorColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeOtpInputColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
