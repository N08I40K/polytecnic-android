package ru.n08i40k.polytechnic.next.network.data.scheduleReplacer

import android.content.Context
import com.android.volley.Response
import ru.n08i40k.polytechnic.next.network.AuthorizedMultipartRequest

class ScheduleReplacerSetReq(
    context: Context,
    private val fileName: String,
    private val fileData: ByteArray,
    private val fileType: String,
    private val listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedMultipartRequest(
    context,
    Method.POST,
    "schedule-replacer/set",
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