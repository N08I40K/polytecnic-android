package ru.n08i40k.polytechnic.next.data.schedule.impl

import android.content.Context
import com.android.volley.toolbox.RequestFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetRequest
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetRequestData
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetResponse
import ru.n08i40k.polytechnic.next.settings.settingsDataStore

class RemoteScheduleRepository(private val context: Context) : ScheduleRepository {


    override suspend fun getGroup(): MyResult<Group> {
        return withContext(Dispatchers.IO) {
            val groupName = runBlocking {
                context.settingsDataStore.data.map { settings -> settings.group }.first()
            }

            if (groupName.isEmpty())
                return@withContext MyResult.Failure(IllegalArgumentException("No group name provided!"))

            val future = RequestFuture.newFuture<ScheduleGetResponse>()
            ScheduleGetRequest(
                ScheduleGetRequestData(groupName),
                context,
                future,
                future
            ).send()

            try {
                MyResult.Success(future.get().group)
            } catch (exception: Exception) {
                MyResult.Failure(exception)
            }
        }
    }
}