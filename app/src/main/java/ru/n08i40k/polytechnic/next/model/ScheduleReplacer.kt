package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleReplacer(
    val etag: String,
    val size: Int
)