package ru.n08i40k.polytechnic.next.network.request.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ScheduleGetCacheStatus(
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context,
    Method.GET,
    "schedule/cache-status",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val cacheUpdateRequired: Boolean,
        val cacheHash: String,
        val lastCacheUpdate: Long,
        val lastScheduleUpdate: Long,
    )
}