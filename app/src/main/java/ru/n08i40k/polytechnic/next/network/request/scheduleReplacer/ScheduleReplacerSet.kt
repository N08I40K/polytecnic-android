package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import ru.n08i40k.polytechnic.next.network.AuthorizedMultipartRequest

class ScheduleReplacerSet(
    context: Context,
    private val fileName: String,
    private val fileData: ByteArray,
    private val fileType: String,
    private val listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedMultipartRequest(
    context,
    Method.POST,
    "v1/schedule-replacer/set",
    { listener.onResponse(null) },
    errorListener
) {
    override val byteData: Map<String, DataPart>
        get() = mapOf(
            Pair(
                "file",
                DataPart(fileName, fileData, fileType)
            )
        )
}