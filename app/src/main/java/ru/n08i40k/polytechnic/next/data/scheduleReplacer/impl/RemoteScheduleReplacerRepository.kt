package ru.n08i40k.polytechnic.next.data.schedule.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.scheduleReplacer.ScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer
import ru.n08i40k.polytechnic.next.network.data.scheduleReplacer.ScheduleReplacerClearReq
import ru.n08i40k.polytechnic.next.network.data.scheduleReplacer.ScheduleReplacerGetReq
import ru.n08i40k.polytechnic.next.network.data.scheduleReplacer.ScheduleReplacerSetReq
import ru.n08i40k.polytechnic.next.network.tryFuture

class RemoteScheduleReplacerRepository(private val context: Context) : ScheduleReplacerRepository {
    override suspend fun getAll(): MyResult<List<ScheduleReplacer>> =
        withContext(Dispatchers.IO) {
            tryFuture { ScheduleReplacerGetReq(context, it, it) }
        }


    override suspend fun setCurrent(
        fileName: String,
        fileData: ByteArray,
        fileType: String
    ): MyResult<Nothing> =
        withContext(Dispatchers.IO) {
            tryFuture { ScheduleReplacerSetReq(context, fileName, fileData, fileType, it, it) }
        }

    override suspend fun clear(): MyResult<Int> {
        val response = withContext(Dispatchers.IO) {
            tryFuture { ScheduleReplacerClearReq(context, it, it) }
        }

        return when (response) {
            is MyResult.Failure -> response
            is MyResult.Success -> MyResult.Success(response.data.count)
        }
    }

}