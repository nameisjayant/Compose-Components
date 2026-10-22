package com.nameisjayant.components.otpinput

import androidx.compose.runtime.Immutable

/**
 * One cell of [ComposeOtpInput], handed to its `cellContent` slot.
 *
 * @property index position from the start, 0-based.
 * @property char the character the user entered here, or null when the cell is empty.
 * @property text what the cell shows: [char], the mask character, or "" when empty.
 * @property isActive this cell gets the next character and the input has focus.
 * @property isError the input is in its error state.
 * @property isEnabled the input is enabled.
 */
@Immutable
public class OtpCell(
    public val index: Int,
    public val char: Char?,
    public val text: String,
    public val isActive: Boolean,
    public val isError: Boolean,
    public val isEnabled: Boolean,
) {

    /** Whether a character has been entered in this cell. */
    public val isFilled: Boolean get() = char != null

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is OtpCell &&
                index == other.index &&
                char == other.char &&
                text == other.text &&
                isActive == other.isActive &&
                isError == other.isError &&
                isEnabled == other.isEnabled
            )

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + char.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + isError.hashCode()
        result = 31 * result + isEnabled.hashCode()
        return result
    }

    override fun toString(): String = "OtpCell(index=$index, char=$char, isActive=$isActive, isError=$isError)"
}
