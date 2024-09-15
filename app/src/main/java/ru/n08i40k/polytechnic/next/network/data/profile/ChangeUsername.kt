package ru.n08i40k.polytechnic.next.network.data.profile

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ChangeUsernameRequest(
    private val data: ChangeUsernameRequestData,
    context: Context,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "users/change-username",
    Response.Listener<String> { listener.onResponse(null) },
    errorListener
) {
    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}