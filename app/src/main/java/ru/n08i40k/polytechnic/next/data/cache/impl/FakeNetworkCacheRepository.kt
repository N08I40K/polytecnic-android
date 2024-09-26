package ru.n08i40k.polytechnic.next.data.cache.impl

import ru.n08i40k.polytechnic.next.CachedResponse
import ru.n08i40k.polytechnic.next.data.cache.NetworkCacheRepository

class FakeNetworkCacheRepository : NetworkCacheRepository {
    override suspend fun get(url: String): CachedResponse? {
        return null
    }

    override suspend fun put(url: String, data: String) {}

    override suspend fun clear() {}

    override suspend fun isHashPresent(): Boolean {
        return true
    }

    override suspend fun setHash(hash: String) {}
}