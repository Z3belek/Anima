package com.aokaze.anima.data.repositories

import com.aokaze.anima.data.entities.Genre
import com.aokaze.anima.data.util.AssetsReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class CachedDataReader<T>(private val reader: suspend () -> List<T>) {
    private val mutex = Mutex()
    private lateinit var cache: List<T>

    suspend fun read(): List<T> {
        mutex.withLock {
            if (!::cache.isInitialized) {
                cache = reader()
            }
        }
        return cache
    }
}

internal suspend fun readGenreData(
    assetsReader: AssetsReader,
    resourceId: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): List<Genre> = withContext(dispatcher) {
    assetsReader.getJsonDataFromAsset(resourceId).map {
        val json = Json { ignoreUnknownKeys = true }
        json.decodeFromString<List<Genre>>(it)
    }.getOrDefault(emptyList())
}