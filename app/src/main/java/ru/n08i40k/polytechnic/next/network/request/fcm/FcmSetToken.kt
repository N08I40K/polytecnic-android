package ru.n08i40k.polytechnic.next.network.request.fcm

import android.content.Context
import com.android.volley.Response
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class FcmSetToken(
    context: Context,
    token: String,
    listener: Response.Listener<Unit>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(
    context, Method.POST,
    "v1/fcm/set-token/$token",
    { listener.onResponse(Unit) },
    errorListener,
    true
)