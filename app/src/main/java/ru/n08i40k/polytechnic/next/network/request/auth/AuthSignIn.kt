package ru.n08i40k.polytechnic.next.network.request.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.RequestBase

class AuthSignIn(
    private val data: RequestDto,
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    context,
    Method.POST,
    "auth/sign-in",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(val username: String, val password: String)

    @Serializable
    data class ResponseDto(val id: String, val accessToken: String, val group: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = super.getHeaders()
        headers["version"] = "2"

        return headers
    }
}