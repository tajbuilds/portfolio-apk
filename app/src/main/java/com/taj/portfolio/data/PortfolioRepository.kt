package com.taj.portfolio.data

import com.google.gson.Gson
import com.taj.portfolio.data.cache.CacheBlobEntity
import com.taj.portfolio.data.cache.CacheDao

class PortfolioRepository(
    private val api: PortfolioApi,
    private val cacheDao: CacheDao,
    private val gson: Gson,
) {
    suspend fun getCachedHome(): HomeResponse? = get(KEY_HOME, HomeResponse::class.java)
    suspend fun getCachedWork(): WorkListResponse? = get(KEY_WORK, WorkListResponse::class.java)
    suspend fun getCachedAbout(): AboutResponse? = get(KEY_ABOUT, AboutResponse::class.java)
    suspend fun getCachedContact(): ContactResponse? = get(KEY_CONTACT, ContactResponse::class.java)
    suspend fun getCachedDetail(slug: String): WorkDetailResponse? =
        get(detailKey(slug), WorkDetailResponse::class.java)

    suspend fun refreshHome(): HomeResponse = api.getHome().also { put(KEY_HOME, it) }
    suspend fun refreshWork(): WorkListResponse = api.getWork().also { put(KEY_WORK, it) }
    suspend fun refreshAbout(): AboutResponse = api.getAbout().also { put(KEY_ABOUT, it) }
    suspend fun refreshContact(): ContactResponse = api.getContact().also { put(KEY_CONTACT, it) }
    suspend fun refreshDetail(slug: String): WorkDetailResponse =
        api.getWorkDetail(slug).also { put(detailKey(slug), it) }

    private suspend fun put(key: String, payload: Any) {
        cacheDao.put(
            CacheBlobEntity(
                key = key,
                payload = gson.toJson(payload),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    private suspend fun <T> get(key: String, clazz: Class<T>): T? {
        val payload = cacheDao.get(key)?.payload ?: return null
        return runCatching { gson.fromJson(payload, clazz) }.getOrNull()
    }

    private fun detailKey(slug: String): String = "$KEY_DETAIL_PREFIX$slug"

    private companion object {
        const val KEY_HOME = "home"
        const val KEY_WORK = "work"
        const val KEY_ABOUT = "about"
        const val KEY_CONTACT = "contact"
        const val KEY_DETAIL_PREFIX = "work_detail_"
    }
}
