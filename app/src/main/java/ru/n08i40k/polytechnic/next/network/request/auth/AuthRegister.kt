package ru.n08i40k.polytechnic.next.network.request.auth

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.RequestBase

class AuthRegister(
    private val data: RequestDto,
    context: Context,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    context,
    Method.POST,
    "auth/sign-up",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(
        val username: String,
        val password: String,
        val group: String,
        val role: UserRole
    )

    @Serializable
    data class ResponseDto(val id: String, val accessToken: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}