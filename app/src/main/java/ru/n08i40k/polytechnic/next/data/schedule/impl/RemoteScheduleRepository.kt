package ru.n08i40k.polytechnic.next.data.schedule.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGet
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetTeacher
import ru.n08i40k.polytechnic.next.network.tryFuture

class RemoteScheduleRepository(private val context: Context) : ScheduleRepository {
    override suspend fun getGroup(): MyResult<GroupOrTeacher> =
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

    override suspend fun getTeacher(name: String): MyResult<GroupOrTeacher> =
        withContext(Dispatchers.IO) {
            val response = tryFuture {
                ScheduleGetTeacher(
                    context,
                    name,
                    it,
                    it
                )
            }

            when (response) {
                is MyResult.Failure -> response
                is MyResult.Success -> MyResult.Success(response.data.teacher)
            }
        }
}