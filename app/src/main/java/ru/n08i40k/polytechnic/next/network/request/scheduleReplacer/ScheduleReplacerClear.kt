package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ScheduleReplacerClear(
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "v1/schedule-replacer/clear",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val count: Int
    )
}