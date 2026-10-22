package com.nameisjayant.components.ratingbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeRatingBar]. "Active" is the filled part of an item, "inactive" the empty part.
 * Borders are only drawn when the bar's `borderWidth` is above zero.
 *
 * Get the theme-based defaults from [ComposeRatingBarDefaults.colors], a palette around one color from
 * [ComposeRatingBarDefaults.accentColors], outline-only empties from [ComposeRatingBarDefaults.outlinedColors],
 * or tweak any of them with [copy].
 */
@Immutable
public class ComposeRatingBarColors(
    public val activeColor: Color,
    public val inactiveColor: Color,
    public val activeBorderColor: Color,
    public val inactiveBorderColor: Color,
    public val disabledActiveColor: Color,
    public val disabledInactiveColor: Color,
    public val haloColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        activeColor: Color = Color.Unspecified,
        inactiveColor: Color = Color.Unspecified,
        activeBorderColor: Color = Color.Unspecified,
        inactiveBorderColor: Color = Color.Unspecified,
        disabledActiveColor: Color = Color.Unspecified,
        disabledInactiveColor: Color = Color.Unspecified,
        haloColor: Color = Color.Unspecified,
    ): ComposeRatingBarColors = ComposeRatingBarColors(
        activeColor = activeColor.or(this.activeColor),
        inactiveColor = inactiveColor.or(this.inactiveColor),
        activeBorderColor = activeBorderColor.or(this.activeBorderColor),
        inactiveBorderColor = inactiveBorderColor.or(this.inactiveBorderColor),
        disabledActiveColor = disabledActiveColor.or(this.disabledActiveColor),
        disabledInactiveColor = disabledInactiveColor.or(this.disabledInactiveColor),
        haloColor = haloColor.or(this.haloColor),
    )

    internal fun active(enabled: Boolean): Color = if (enabled) activeColor else disabledActiveColor
    internal fun inactive(enabled: Boolean): Color = if (enabled) inactiveColor else disabledInactiveColor
    internal fun activeBorder(enabled: Boolean): Color = if (enabled) activeBorderColor else disabledActiveColor
    internal fun inactiveBorder(enabled: Boolean): Color = if (enabled) inactiveBorderColor else disabledInactiveColor

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        activeColor, inactiveColor, activeBorderColor, inactiveBorderColor,
        disabledActiveColor, disabledInactiveColor, haloColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeRatingBarColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
