package com.taj.portfolio.data

import com.google.gson.Gson
import com.taj.portfolio.data.cache.CacheBlobEntity
import com.taj.portfolio.data.cache.CacheDao
import java.time.Instant

class PortfolioRepository(
    private val api: PortfolioApi,
    private val cacheDao: CacheDao,
    private val gson: Gson,
) {
    suspend fun getCachedHome(): CachedResult<HomeResponse> =
        get(KEY_HOME, HomeResponse::class.java, HOME_MAX_AGE_MS)

    suspend fun getCachedWork(): CachedResult<WorkListResponse> =
        get(KEY_WORK, WorkListResponse::class.java, WORK_MAX_AGE_MS)

    suspend fun getCachedAbout(): CachedResult<AboutResponse> =
        get(KEY_ABOUT, AboutResponse::class.java, ABOUT_MAX_AGE_MS)

    suspend fun getCachedContact(): CachedResult<ContactResponse> =
        get(KEY_CONTACT, ContactResponse::class.java, CONTACT_MAX_AGE_MS)

    suspend fun getCachedDetail(slug: String): CachedResult<WorkDetailResponse> =
        get(detailKey(slug), WorkDetailResponse::class.java, DETAIL_MAX_AGE_MS)

    suspend fun refreshHome(): HomeResponse =
        api.getHome().toDomain().also { validateVersion(it); put(KEY_HOME, it) }

    suspend fun refreshWork(): WorkListResponse =
        api.getWork().toDomain().also { validateVersion(it); put(KEY_WORK, it) }

    suspend fun refreshAbout(): AboutResponse =
        api.getAbout().toDomain().also { validateVersion(it); put(KEY_ABOUT, it) }

    suspend fun refreshContact(): ContactResponse =
        api.getContact().toDomain().also { validateVersion(it); put(KEY_CONTACT, it) }

    suspend fun refreshDetail(slug: String): WorkDetailResponse =
        api.getWorkDetail(slug).toDomain().also { validateVersion(it); put(detailKey(slug), it) }

    suspend fun clearCache() = cacheDao.clearAll()

    private suspend fun put(key: String, payload: Any) {
        val generatedAtEpoch = (payload as? MobileEnvelope)?.generatedAt?.let(::toEpochMillis)
        val now = generatedAtEpoch ?: System.currentTimeMillis()
        cacheDao.deleteOlderThan(now - HARD_RETENTION_MS)
        cacheDao.put(
            CacheBlobEntity(
                key = key,
                payload = gson.toJson(payload),
                updatedAt = now,
            ),
        )
    }

    private suspend fun <T> get(key: String, clazz: Class<T>, maxAgeMs: Long): CachedResult<T> {
        val cached = cacheDao.get(key) ?: return CachedResult(value = null, isStale = true)
        val parsed = runCatching { gson.fromJson(cached.payload, clazz) }.getOrNull()
        if (parsed == null) {
            cacheDao.delete(key)
            return CachedResult(value = null, isStale = true)
        }
        return CachedResult(
            value = parsed,
            isStale = isCacheStale(cached.updatedAt, System.currentTimeMillis(), maxAgeMs),
            updatedAt = cached.updatedAt,
            generatedAt = (parsed as? MobileEnvelope)?.generatedAt,
        )
    }

    private fun detailKey(slug: String): String = "$KEY_DETAIL_PREFIX$slug"

    private fun validateVersion(envelope: MobileEnvelope) {
        val rawVersion = envelope.version.trim()
        val major = rawVersion.substringBefore(".")
        if (major != SUPPORTED_API_MAJOR) {
            throw IllegalStateException(
                "Unsupported API version: $rawVersion (expected major $SUPPORTED_API_MAJOR)",
            )
        }
    }

    private fun toEpochMillis(value: String): Long? = runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()

    private companion object {
        const val SUPPORTED_API_MAJOR = "1"
        const val KEY_HOME = "home"
        const val KEY_WORK = "work"
        const val KEY_ABOUT = "about"
        const val KEY_CONTACT = "contact"
        const val KEY_DETAIL_PREFIX = "work_detail_"
        const val HOME_MAX_AGE_MS = 15 * 60 * 1000L
        const val WORK_MAX_AGE_MS = 15 * 60 * 1000L
        const val ABOUT_MAX_AGE_MS = 12 * 60 * 60 * 1000L
        const val CONTACT_MAX_AGE_MS = 12 * 60 * 60 * 1000L
        const val DETAIL_MAX_AGE_MS = 60 * 60 * 1000L
        const val HARD_RETENTION_MS = 30L * 24 * 60 * 60 * 1000L
    }
}

data class CachedResult<T>(
    val value: T?,
    val isStale: Boolean,
    val updatedAt: Long? = null,
    val generatedAt: String? = null,
)
