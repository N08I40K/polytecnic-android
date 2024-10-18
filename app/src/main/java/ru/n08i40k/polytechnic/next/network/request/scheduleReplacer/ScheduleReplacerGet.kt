package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ScheduleReplacerGet(
    context: Context,
    listener: Response.Listener<List<ScheduleReplacer>>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    context,
    Method.GET,
    "v1/schedule-replacer/get",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)