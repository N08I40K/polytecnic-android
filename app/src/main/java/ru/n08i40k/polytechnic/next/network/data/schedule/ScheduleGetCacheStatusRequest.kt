package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleGetCacheStatusRequest(
    context: Context,
    listener: Response.Listener<ScheduleGetCacheStatusResponse>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context, Method.GET, "schedule/cache-status", Response.Listener<String> { response ->
        listener.onResponse(Json.decodeFromString<ScheduleGetCacheStatusResponse>(response))
    }, errorListener
)