package ru.n08i40k.polytechnic.next.network.request.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.network.request.CachedRequest

class ScheduleGetTeacher(
    context: Context,
    teacher: String,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context,
    Method.GET,
    "v2/schedule/teacher/$teacher",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val updatedAt: String,
        val teacher: GroupOrTeacher,
        val updated: ArrayList<Int>,
    )
}