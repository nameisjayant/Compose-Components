package com.nameisjayant.components.colorpicker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ColorMathTest {

    @Test
    fun hsvPacksTheExpectedColors() {
        assertEquals(0xFFFF0000.toInt(), hsvToArgb(0f, 1f, 1f, 1f))
        assertEquals(0xFFFF0000.toInt(), hsvToArgb(360f, 1f, 1f, 1f))
        assertEquals(0xFF00FF00.toInt(), hsvToArgb(120f, 1f, 1f, 1f))
        assertEquals(0xFF0000FF.toInt(), hsvToArgb(240f, 1f, 1f, 1f))
        assertEquals(0xFFFFFFFF.toInt(), hsvToArgb(77f, 0f, 1f, 1f))
        assertEquals(0xFF000000.toInt(), hsvToArgb(77f, 1f, 0f, 1f))
        assertEquals(0x80FF8000.toInt(), hsvToArgb(30f, 1f, 1f, 128 / 255f))
    }

    @Test
    fun everyColorSurvivesTheRoundTrip() {
        for (r in 0..255 step 15) for (g in 0..255 step 17) for (b in 0..255 step 5) {
            val argb = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            assertEquals(formatHex(argb, true), argb, hsvFromArgb(argb).toArgb())
        }
    }

    @Test
    fun graysAndBlackKeepThePreviousHue() {
        val previous = HsvColor(210f, 0.6f, 0.8f)
        val gray = hsvFromArgb(0xFF808080.toInt(), previous)
        assertEquals(210f, gray.hue)
        assertEquals(0f, gray.saturation)
        val black = hsvFromArgb(0xFF000000.toInt(), previous)
        assertEquals(210f, black.hue)
        assertEquals(0.6f, black.saturation)
        assertEquals(0f, black.value)
        assertEquals(0f, hsvFromArgb(0xFF808080.toInt()).hue)
    }

    @Test
    fun hsvColorClampsAndWraps() {
        val color = HsvColor(hue = -30f, saturation = 2f, value = -1f, alpha = Float.NaN)
        assertEquals(330f, color.hue)
        assertEquals(1f, color.saturation)
        assertEquals(0f, color.value)
        assertEquals(0f, color.alpha)
        assertEquals(360f, HsvColor(360f, 1f, 1f).hue)
        assertEquals(30f, HsvColor(390f, 1f, 1f).hue)
    }

    @Test
    fun hexFormatsWithAndWithoutAlpha() {
        assertEquals("80FF8000", formatHex(0x80FF8000.toInt(), includeAlpha = true))
        assertEquals("FF8000", formatHex(0x80FF8000.toInt(), includeAlpha = false))
        assertEquals("000000", formatHex(0xFF000000.toInt(), includeAlpha = false))
    }

    @Test
    fun hexParsesEveryLength() {
        assertEquals(0xFF3D5AFE.toInt(), parseHex("#3d5afe"))
        assertEquals(0x803D5AFE.toInt(), parseHex("3D5AFE", alpha = 0x80))
        assertEquals(0x403D5AFE, parseHex("403D5AFE", alpha = 0x80))
        assertEquals(0xFFAABBCC.toInt(), parseHex("ABC"))
        assertEquals(0x88AABBCC.toInt(), parseHex("#8ABC"))
        assertNull(parseHex(""))
        assertNull(parseHex("12345"))
        assertNull(parseHex("GG0000"))
    }

    @Test
    fun hexInputIsSanitized() {
        assertEquals("3D5AFE", sanitizeHex("#3d 5a-fe", 8))
        assertEquals("ABCDEF", sanitizeHex("abcdef12", 6))
    }
}
