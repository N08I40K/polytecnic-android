package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.CachedRequest

class ScheduleGetReq(
    private val data: ScheduleGetReqData,
    context: Context,
    listener: Response.Listener<ScheduleGetResData>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    context,
    Method.POST,
    "schedule/get-group",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}