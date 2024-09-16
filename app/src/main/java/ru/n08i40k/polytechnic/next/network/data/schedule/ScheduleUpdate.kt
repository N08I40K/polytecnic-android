package ru.n08i40k.polytechnic.next.network.data.schedule

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleUpdateRequest(
    private val data: ScheduleUpdateRequestData,
    context: Context,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    context, Method.POST, "schedule/update-site-main-page", Response.Listener<String> {
        listener.onResponse(null)
    }, errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}