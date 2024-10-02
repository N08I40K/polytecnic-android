package ru.n08i40k.polytechnic.next.network.data.schedule

import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.model.Group

@Serializable
data class ScheduleGetResData(
    val updatedAt: String,
    val group: Group,
    val lastChangedDays: ArrayList<Int>,
)