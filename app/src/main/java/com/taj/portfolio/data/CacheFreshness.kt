package com.taj.portfolio.data

fun isCacheStale(updatedAtMillis: Long, nowMillis: Long, maxAgeMillis: Long): Boolean {
    if (maxAgeMillis <= 0L) return true
    return nowMillis - updatedAtMillis > maxAgeMillis
}
