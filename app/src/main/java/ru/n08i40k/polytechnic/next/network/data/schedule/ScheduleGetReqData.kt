package ru.n08i40k.polytechnic.next.network.data.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleGetReqData(val name: String)