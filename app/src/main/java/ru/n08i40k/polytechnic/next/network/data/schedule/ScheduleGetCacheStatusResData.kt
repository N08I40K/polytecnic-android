package ru.n08i40k.polytechnic.next.network.data.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleGetCacheStatusResData(
    val cacheUpdateRequired: Boolean,
    val cacheHash: String,
    val lastCacheUpdate: Long,
    val lastScheduleUpdate: Long,
)