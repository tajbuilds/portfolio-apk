package com.taj.portfolio.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache_blob WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): CacheBlobEntity?

    @Query("DELETE FROM cache_blob WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM cache_blob WHERE updatedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    @Query("DELETE FROM cache_blob")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(blob: CacheBlobEntity)
}
