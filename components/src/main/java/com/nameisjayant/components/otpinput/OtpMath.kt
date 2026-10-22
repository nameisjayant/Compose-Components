package com.nameisjayant.components.otpinput

/**
 * Turns the text field's new raw text into the code to keep: drops characters [accept] rejects,
 * uppercases when asked and caps the result at [length].
 *
 * When the inserted part alone fills the whole code (a pasted or autofilled code), it replaces the old
 * code instead of being appended to it; extra typing on a full code is ignored.
 */
internal fun sanitizeOtp(
    old: String,
    new: String,
    length: Int,
    uppercase: Boolean,
    accept: (Char) -> Boolean,
): String {
    fun clean(text: String) = buildString {
        for (c in text) {
            val char = if (uppercase) c.uppercaseChar() else c
            if (accept(char)) append(char)
        }
    }

    val inserted = clean(insertedText(old, new))
    if (inserted.length >= length) return inserted.take(length)
    return clean(new).take(length)
}

/** The part of [new] that isn't shared with [old] at either end. */
internal fun insertedText(old: String, new: String): String {
    val prefix = new.commonPrefixWith(old).length
    var suffix = 0
    val maxSuffix = minOf(new.length, old.length) - prefix
    while (suffix < maxSuffix && new[new.length - 1 - suffix] == old[old.length - 1 - suffix]) suffix++
    return new.substring(prefix, new.length - suffix)
}

/** Index of the cell that gets the next character: the first empty one, or the last when full. */
internal fun activeCellIndex(filled: Int, length: Int): Int = filled.coerceIn(0, length - 1)

/** Whether a separator goes after cell [index] when cells are grouped by [groupSize] (0 = no groups). */
internal fun separatorAfter(index: Int, groupSize: Int, length: Int): Boolean =
    groupSize > 0 && index < length - 1 && (index + 1) % groupSize == 0
