package com.taj.portfolio.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ApiMappersTest {
    @Test
    fun `maps sparse contact dto with safe defaults`() {
        val dto = ContactResponseDto(
            version = null,
            generatedAt = "2026-03-08T00:00:00Z",
            contact = ContactDto(
                email = null,
                formPath = null,
                turnstileRequired = null,
                links = null,
            ),
        )

        val mapped = dto.toDomain()

        assertEquals("1.0", mapped.version)
        assertEquals("2026-03-08T00:00:00Z", mapped.generatedAt)
        assertEquals("", mapped.contact.email)
        assertEquals("", mapped.contact.formPath)
        assertFalse(mapped.contact.turnstileRequired)
        assertEquals(0, mapped.contact.links.size)
    }
}
