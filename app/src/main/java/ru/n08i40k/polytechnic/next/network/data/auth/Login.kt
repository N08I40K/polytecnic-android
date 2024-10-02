package ru.n08i40k.polytechnic.next.network.data.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.RequestBase

class LoginRequest(
    private val data: LoginRequestData,
    context: Context,
    listener: Response.Listener<LoginResponseData>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    context,
    Method.POST,
    "auth/sign-in",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}