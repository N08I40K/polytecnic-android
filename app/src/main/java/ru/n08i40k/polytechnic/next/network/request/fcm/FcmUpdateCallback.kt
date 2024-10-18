package ru.n08i40k.polytechnic.next.network.request.fcm

import android.content.Context
import com.android.volley.Response
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class FcmUpdateCallback(
    context: Context,
    version: String,
    listener: Response.Listener<Unit>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(
    context, Method.POST,
    "v1/fcm/update-callback/$version",
    { listener.onResponse(Unit) },
    errorListener,
    true
)