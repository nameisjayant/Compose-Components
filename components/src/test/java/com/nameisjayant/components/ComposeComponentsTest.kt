package com.nameisjayant.components

import org.junit.Assert.assertTrue
import org.junit.Test

class ComposeComponentsTest {
    @Test
    fun versionIsSet() {
        assertTrue(ComposeComponents.VERSION.isNotBlank())
    }
}
