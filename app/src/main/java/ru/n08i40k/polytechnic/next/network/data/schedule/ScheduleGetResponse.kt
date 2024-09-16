package ru.n08i40k.polytechnic.next.network.data.schedule

import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.model.Group

@Serializable
data class ScheduleGetResponse(
    val updatedAt: String,
    val group: Group,
    val etag: String,
    val lastChangedDays: ArrayList<Int>,
    val updateRequired: Boolean
)