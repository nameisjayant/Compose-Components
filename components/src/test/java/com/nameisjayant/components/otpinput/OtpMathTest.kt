package com.nameisjayant.components.otpinput

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OtpMathTest {

    private fun digits(old: String, new: String, length: Int = 6) =
        sanitizeOtp(old, new, length, uppercase = false, accept = OtpInputType.Digits::accepts)

    @Test
    fun typingAppendsAndBackspaceRemoves() {
        assertEquals("12", digits("1", "12"))
        assertEquals("1", digits("12", "1"))
        assertEquals("", digits("1", ""))
    }

    @Test
    fun rejectedCharactersAreDropped() {
        assertEquals("1", digits("1", "1a"))
        assertEquals("12", digits("1", "1 2"))
    }

    @Test
    fun typingOnAFullCodeIsIgnored() {
        assertEquals("123456", digits("123456", "1234567"))
    }

    @Test
    fun pastedFullCodeReplacesTheOldOne() {
        assertEquals("482913", digits("12", "12482-913"))
        assertEquals("482913", digits("", "Your code is 482 913."))
        assertEquals("123456", digits("", "12345678"))
    }

    @Test
    fun shortPasteIsAppended() {
        assertEquals("1234", digits("12", "1234"))
    }

    @Test
    fun uppercaseAppliesBeforeFiltering() {
        val code = sanitizeOtp("", "ab-c1", 4, uppercase = true) { it in 'A'..'Z' }
        assertEquals("ABC", code)
    }

    @Test
    fun insertedTextHandlesRepeatedCharacters() {
        assertEquals("1", insertedText("11", "111"))
        assertEquals("x", insertedText("ab", "axb"))
        assertEquals("", insertedText("abc", "ab"))
    }

    @Test
    fun inputTypesAcceptTheRightCharacters() {
        assertTrue(OtpInputType.Digits.accepts('7'))
        assertFalse(OtpInputType.Digits.accepts('٣')) // Arabic-Indic digit
        assertTrue(OtpInputType.Letters.accepts('k'))
        assertFalse(OtpInputType.Letters.accepts('3'))
        assertTrue(OtpInputType.Alphanumeric.accepts('3'))
        assertFalse(OtpInputType.Alphanumeric.accepts('-'))
        assertTrue(OtpInputType.Any.accepts('-'))
        assertFalse(OtpInputType.Any.accepts(' '))
    }

    @Test
    fun activeCellIsFirstEmptyOrLast() {
        assertEquals(0, activeCellIndex(0, 6))
        assertEquals(3, activeCellIndex(3, 6))
        assertEquals(5, activeCellIndex(6, 6))
    }

    @Test
    fun separatorsSplitGroups() {
        val after = (0 until 6).filter { separatorAfter(it, 3, 6) }
        assertEquals(listOf(2), after)
        assertEquals(listOf(1, 3), (0 until 6).filter { separatorAfter(it, 2, 6) })
        assertTrue((0 until 6).none { separatorAfter(it, 0, 6) })
    }
}
