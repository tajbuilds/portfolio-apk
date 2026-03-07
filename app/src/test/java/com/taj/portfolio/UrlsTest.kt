package com.taj.portfolio

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlsTest {
    @Test
    fun `keeps absolute https url unchanged`() {
        val baseUrl = "https://tajs.io/"
        val absolute = "https://cdn.tajs.io/image.png"

        assertEquals(absolute, resolveUrl(baseUrl, absolute))
    }

    @Test
    fun `joins relative path without duplicate slash`() {
        val baseUrl = "https://tajs.io/"
        val relative = "/api/mobile/home"

        assertEquals("https://tajs.io/api/mobile/home", resolveUrl(baseUrl, relative))
    }
}
