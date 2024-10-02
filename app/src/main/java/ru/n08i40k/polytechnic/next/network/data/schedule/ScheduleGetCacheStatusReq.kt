package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleGetCacheStatusReq(
    context: Context,
    listener: Response.Listener<ScheduleGetCacheStatusResData>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context,
    Method.GET,
    "schedule/cache-status",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)