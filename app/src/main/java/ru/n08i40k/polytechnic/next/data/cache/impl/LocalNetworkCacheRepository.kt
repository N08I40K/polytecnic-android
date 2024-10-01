package ru.n08i40k.polytechnic.next.data.cache.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.CachedResponse
import ru.n08i40k.polytechnic.next.UpdateDates
import ru.n08i40k.polytechnic.next.data.cache.NetworkCacheRepository
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import javax.inject.Inject

class LocalNetworkCacheRepository
@Inject constructor(private val applicationContext: Context) : NetworkCacheRepository {
    private val cacheMap: MutableMap<String, CachedResponse> = mutableMapOf()
    private var updateDates: UpdateDates = UpdateDates.newBuilder().build()
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
        // Если кешированого ответа нет, то возвращаем null
        // Если хеши не совпадают и локальный хеш присутствует, то возвращаем null

        val response = cacheMap[url] ?: return null

        if (response.hash != this.hash && this.hash != null)
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
            save()
        }
    }

    override suspend fun getUpdateDates(): UpdateDates {
        return this.updateDates
    }

    override suspend fun setUpdateDates(cache: Long, schedule: Long) {
        updateDates = UpdateDates
            .newBuilder()
            .setCache(cache)
            .setSchedule(schedule).build()

        withContext(Dispatchers.IO) {
            applicationContext.settingsDataStore.updateData {
                it
                    .toBuilder()
                    .setUpdateDates(updateDates)
                    .build()
            }
        }
        save()
    }

    private suspend fun save() {
        withContext(Dispatchers.IO) {
            applicationContext.settingsDataStore.updateData {
                it
                    .toBuilder()
                    .putAllCacheStorage(cacheMap)
                    .build()
            }
        }
    }
}