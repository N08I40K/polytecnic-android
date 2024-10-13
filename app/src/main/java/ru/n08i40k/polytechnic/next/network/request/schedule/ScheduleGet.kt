package ru.n08i40k.polytechnic.next.network.request.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.network.request.CachedRequest

class ScheduleGet(
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context,
    Method.POST,
    "schedule/get-group",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(val name: String)

    @Serializable
    data class ResponseDto(
        val updatedAt: String,
        val group: Group,
        val lastChangedDays: ArrayList<Int>,
    )
}