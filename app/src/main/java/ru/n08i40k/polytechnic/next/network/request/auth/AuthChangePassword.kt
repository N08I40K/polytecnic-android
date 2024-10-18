package ru.n08i40k.polytechnic.next.network.request.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class AuthChangePassword(
    private val data: RequestDto,
    context: Context,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "v1/auth/change-password",
    { listener.onResponse(null) },
    errorListener,
    canBeUnauthorized = true
) {
    @Serializable
    data class RequestDto(val oldPassword: String, val newPassword: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}