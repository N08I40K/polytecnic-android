package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleGetRequest(
    private val data: ScheduleGetRequestData,
    context: Context,
    listener: Response.Listener<ScheduleGetResponse>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context, Method.POST, "schedule/get-group", Response.Listener<String> { response ->
        listener.onResponse(Json.decodeFromString<ScheduleGetResponse>(response))
    }, errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}