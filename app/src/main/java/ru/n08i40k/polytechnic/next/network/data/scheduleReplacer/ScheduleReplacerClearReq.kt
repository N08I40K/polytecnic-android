package ru.n08i40k.polytechnic.next.network.data.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.data.AuthorizedRequest

class ScheduleReplacerClearReq(
    context: Context,
    listener: Response.Listener<ScheduleReplacerClearResData>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.POST,
    "schedule-replacer/clear",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)