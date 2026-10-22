package com.nameisjayant.components.otpinput

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How [ComposeOtpInput] moves.
 *
 * A new character slides up into its cell ([slideInText]) while the cell bumps to [popScale]; the
 * cursor blinks ([blinkCursor]); and the whole row shakes by [shakeDistance] when the input turns to
 * its error state ([shakeOnError]). Border and container colors always fade between states.
 *
 * Use [None] to switch every animation off.
 */
@Immutable
public class OtpInputAnimation(
    public val enabled: Boolean = true,
    public val slideInText: Boolean = true,
    public val popScale: Float = 1.08f,
    public val blinkCursor: Boolean = true,
    public val shakeOnError: Boolean = true,
    public val shakeDistance: Dp = 10.dp,
) {

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is OtpInputAnimation &&
                enabled == other.enabled &&
                slideInText == other.slideInText &&
                popScale == other.popScale &&
                blinkCursor == other.blinkCursor &&
                shakeOnError == other.shakeOnError &&
                shakeDistance == other.shakeDistance
            )

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + slideInText.hashCode()
        result = 31 * result + popScale.hashCode()
        result = 31 * result + blinkCursor.hashCode()
        result = 31 * result + shakeOnError.hashCode()
        result = 31 * result + shakeDistance.hashCode()
        return result
    }

    public companion object {
        public val Default: OtpInputAnimation = OtpInputAnimation()

        /** Characters appear instantly, the cursor stays solid and errors don't shake. */
        public val None: OtpInputAnimation = OtpInputAnimation(enabled = false)
    }
}
