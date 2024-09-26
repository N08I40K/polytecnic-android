package ru.n08i40k.polytechnic.next.data.cache.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.CachedResponse
import ru.n08i40k.polytechnic.next.data.cache.NetworkCacheRepository
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import javax.inject.Inject

class LocalNetworkCacheRepository
@Inject constructor(private val applicationContext: Context) : NetworkCacheRepository {
    private val cacheMap: MutableMap<String, CachedResponse> = mutableMapOf()
    private var hash: String? = null

    init {
        cacheMap.clear()

        runBlocking {
            cacheMap.putAll(
                applicationContext
                    .settingsDataStore
                    .data
                    .map { settings -> settings.cacheStorageMap }.first()
            )
        }
    }

    override suspend fun get(url: String): CachedResponse? {
        if (this.hash == null)
            return null

        val response = cacheMap[url]

        if (response?.hash != this.hash)
            return null

        return response
    }

    override suspend fun put(url: String, data: String) {
        if (hash == null)
            throw IllegalStateException("Не установлен хеш!")

        cacheMap[url] = CachedResponse
            .newBuilder()
            .setHash(this.hash)
            .setData(data)
            .build()

        save()
    }

    override suspend fun clear() {
        this.cacheMap.clear()
        this.save()
    }

    override suspend fun isHashPresent(): Boolean {
        return this.hash != null
    }

    override suspend fun setHash(hash: String) {
        val freshHash = this.hash == null

        if (!freshHash && this.hash != hash)
            clear()

        this.hash = hash

        if (freshHash) {
            this.cacheMap
                .mapNotNull { if (it.value.hash != this.hash) it.key else null }
                .forEach { this.cacheMap.remove(it) }
        }
    }

    private suspend fun save() {
        withContext(Dispatchers.IO) {
            runBlocking {
                applicationContext.settingsDataStore.updateData {
                    it
                        .toBuilder()
                        .putAllCacheStorage(cacheMap)
                        .build()
                }
            }
        }
    }
}