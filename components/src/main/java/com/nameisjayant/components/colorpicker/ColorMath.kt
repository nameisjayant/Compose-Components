package com.nameisjayant.components.colorpicker

import kotlin.math.abs
import kotlin.math.roundToInt

// Pure color math on packed 0xAARRGGBB ints, kept free of Compose so it can be unit tested on the JVM.

/** Packs an HSV color ([hue] in degrees, the rest 0..1) into 0xAARRGGBB. */
internal fun hsvToArgb(hue: Float, saturation: Float, value: Float, alpha: Float): Int {
    val h = ((hue % 360f) + 360f) % 360f / 60f
    val c = value * saturation
    val x = c * (1f - abs(h % 2f - 1f))
    val m = value - c
    val (r, g, b) = when (h.toInt()) {
        0 -> Triple(c, x, 0f)
        1 -> Triple(x, c, 0f)
        2 -> Triple(0f, c, x)
        3 -> Triple(0f, x, c)
        4 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return (channel(alpha) shl 24) or (channel(r + m) shl 16) or (channel(g + m) shl 8) or channel(b + m)
}

private fun channel(fraction: Float): Int = (fraction * 255f).roundToInt().coerceIn(0, 255)

/**
 * The [HsvColor] for [argb]. Grays have no hue, and black has no saturation either; those come from
 * [previous] so dragging brightness to 0 and back up doesn't reset the hue to red.
 */
internal fun hsvFromArgb(argb: Int, previous: HsvColor? = null): HsvColor {
    val r = (argb shr 16 and 0xFF) / 255f
    val g = (argb shr 8 and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    val alpha = (argb ushr 24) / 255f
    val max = maxOf(r, g, b)
    val delta = max - minOf(r, g, b)
    if (max == 0f) return HsvColor(previous?.hue ?: 0f, previous?.saturation ?: 0f, 0f, alpha)
    if (delta == 0f) return HsvColor(previous?.hue ?: 0f, 0f, max, alpha)
    val hue = when (max) {
        r -> 60f * (((g - b) / delta) % 6f)
        g -> 60f * ((b - r) / delta + 2f)
        else -> 60f * ((r - g) / delta + 4f)
    }
    return HsvColor((hue + 360f) % 360f, delta / max, max, alpha)
}

/** "RRGGBB", or "AARRGGBB" with [includeAlpha], upper case and without the '#'. */
internal fun formatHex(argb: Int, includeAlpha: Boolean): String {
    val digits = (argb.toLong() and 0xFFFFFFFFL).toString(16).uppercase().padStart(8, '0')
    return if (includeAlpha) digits else digits.substring(2)
}

/**
 * Parses "RGB", "ARGB", "RRGGBB" or "AARRGGBB", with or without a leading '#'. Forms without alpha
 * take [alpha] (0..255). Returns null for anything else.
 */
internal fun parseHex(text: String, alpha: Int = 0xFF): Int? {
    val digits = text.trim().removePrefix("#")
    if (digits.any { it.digitToIntOrNull(16) == null }) return null
    val expanded = when (digits.length) {
        3, 4 -> digits.map { "$it$it" }.joinToString("")
        6, 8 -> digits
        else -> return null
    }
    val value = expanded.toLong(16).toInt()
    return if (expanded.length == 8) value else (alpha.coerceIn(0, 255) shl 24) or value
}

/** Keeps the hex digits of [text], upper-cased, at most [maxLength] of them. */
internal fun sanitizeHex(text: String, maxLength: Int): String =
    text.filter { it.digitToIntOrNull(16) != null }.uppercase().take(maxLength)
