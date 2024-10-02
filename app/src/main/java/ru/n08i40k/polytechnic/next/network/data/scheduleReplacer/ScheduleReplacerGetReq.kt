package ru.n08i40k.polytechnic.next.network.data.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleReplacerGetReq(
    context: Context,
    listener: Response.Listener<ScheduleReplacerGetResData>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.GET,
    "schedule-replacer/get",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)