package ru.n08i40k.polytechnic.next.network.data.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ChangePasswordRequest(
    private val data: ChangePasswordRequestData,
    context: Context,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "auth/change-password",
    Response.Listener<String> { listener.onResponse(null) },
    errorListener,
    canBeUnauthorized = true
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}