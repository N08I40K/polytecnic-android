package ru.n08i40k.polytechnic.next.network.request.profile

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ProfileChangeUsername(
    private val data: RequestDto,
    context: Context,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "v1/users/change-username",
    { listener.onResponse(null) },
    errorListener
) {
    @Serializable
    data class RequestDto(val username: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}