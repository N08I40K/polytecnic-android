package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.CachedRequest

class ScheduleGetGroupNamesRequest(
    context: Context,
    listener: Response.Listener<ScheduleGetGroupNamesResponseData>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context, Method.GET, "schedule/get-group-names", Response.Listener<String> { response ->
        listener.onResponse(Json.decodeFromString<ScheduleGetGroupNamesResponseData>(response))
    }, errorListener
)