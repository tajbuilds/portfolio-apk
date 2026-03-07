package com.taj.portfolio.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheFreshnessTest {
    @Test
    fun `cache is fresh at max age boundary`() {
        val updatedAt = 1_000L
        val now = 6_000L
        val maxAge = 5_000L

        assertFalse(isCacheStale(updatedAt, now, maxAge))
    }

    @Test
    fun `cache is stale after max age boundary`() {
        val updatedAt = 1_000L
        val now = 6_001L
        val maxAge = 5_000L

        assertTrue(isCacheStale(updatedAt, now, maxAge))
    }

    @Test
    fun `non positive max age is always stale`() {
        assertTrue(isCacheStale(updatedAtMillis = 100, nowMillis = 200, maxAgeMillis = 0))
    }
}
