package ru.n08i40k.polytechnic.next.network.request.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ScheduleUpdate(
    private val data: RequestDto,
    context: Context,
    listener: Response.Listener<ScheduleGetCacheStatus.ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context,
    Method.PATCH,
    "v4/schedule/update-download-url",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(val url: String)

    override fun getBody(): ByteArray = Json.encodeToString(data).toByteArray()
}
