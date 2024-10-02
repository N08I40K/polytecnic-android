package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.CachedRequest

class ScheduleGetGroupNamesReq(
    context: Context,
    listener: Response.Listener<ScheduleGetGroupNamesResData>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context,
    Method.GET,
    "schedule/get-group-names",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)