package com.nameisjayant.components.otpinput

import androidx.compose.ui.text.input.KeyboardType

/**
 * What [ComposeOtpInput] accepts, and the keyboard it opens by default.
 * For anything else, pass your own `charFilter` to the input.
 */
public enum class OtpInputType(public val keyboardType: KeyboardType) {
    /** 0–9 only, on the number pad. */
    Digits(KeyboardType.NumberPassword),

    /** Letters only. */
    Letters(KeyboardType.Ascii),

    /** Letters and digits. */
    Alphanumeric(KeyboardType.Ascii),

    /** Any visible character (no spaces). */
    Any(KeyboardType.Ascii),
    ;

    /** Whether [char] can go into a cell. */
    public fun accepts(char: Char): Boolean = when (this) {
        Digits -> char in '0'..'9'
        Letters -> char.isLetter()
        Alphanumeric -> char.isLetter() || char in '0'..'9'
        Any -> !char.isWhitespace() && !char.isISOControl()
    }
}

/** How each cell of [ComposeOtpInput] is drawn. */
public enum class OtpCellStyle {
    /** A border around each cell. */
    Outlined,

    /** A tinted cell; the focused cell gets a border. */
    Filled,

    /** A line under each cell. */
    Underlined,
}
