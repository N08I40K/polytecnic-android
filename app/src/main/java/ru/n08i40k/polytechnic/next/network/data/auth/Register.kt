package ru.n08i40k.polytechnic.next.network.data.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.RequestBase

class RegisterRequest(
    private val data: RegisterRequestData,
    context: Context,
    listener: Response.Listener<RegisterResponseData>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    context,
    Method.POST,
    "auth/sign-up",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}