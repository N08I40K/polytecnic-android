package ru.n08i40k.polytechnic.next.network.request.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.CachedRequest

class ScheduleGetGroupNames(
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context,
    Method.GET,
    "schedule/get-group-names",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val names: ArrayList<String>,
    )
}