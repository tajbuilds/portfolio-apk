package com.taj.portfolio.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_blob")
data class CacheBlobEntity(
    @PrimaryKey val key: String,
    val payload: String,
    val updatedAt: Long,
)
