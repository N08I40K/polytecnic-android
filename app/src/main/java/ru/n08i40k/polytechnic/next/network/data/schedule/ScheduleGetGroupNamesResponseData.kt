package ru.n08i40k.polytechnic.next.network.data.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleGetGroupNamesResponseData(
    val names: ArrayList<String>,
)