package ru.n08i40k.polytechnic.next.data.schedule.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGet
import ru.n08i40k.polytechnic.next.network.tryFuture

class RemoteScheduleRepository(private val context: Context) : ScheduleRepository {
    override suspend fun getGroup(): MyResult<Group> =
        withContext(Dispatchers.IO) {
            val response = tryFuture {
                ScheduleGet(
                    context,
                    it,
                    it
                )
            }

            when (response) {
                is MyResult.Failure -> response
                is MyResult.Success -> MyResult.Success(response.data.group)
            }
        }
}