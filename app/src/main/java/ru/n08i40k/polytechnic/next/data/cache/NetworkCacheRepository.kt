package ru.n08i40k.polytechnic.next.data.cache

import ru.n08i40k.polytechnic.next.CachedResponse

interface NetworkCacheRepository {
    suspend fun put(url: String, data: String)

    suspend fun get(url: String): CachedResponse?

    suspend fun clear()

    suspend fun isHashPresent(): Boolean

    suspend fun setHash(hash: String)
}