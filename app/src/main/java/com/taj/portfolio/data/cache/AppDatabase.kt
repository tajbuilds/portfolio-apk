package com.taj.portfolio.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CacheBlobEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}
