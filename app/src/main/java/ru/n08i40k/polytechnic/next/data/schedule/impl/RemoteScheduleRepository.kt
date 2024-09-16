package ru.n08i40k.polytechnic.next.data.schedule.impl

import android.content.Context
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.network.NetworkConnection
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetRequest
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetRequestData
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetResponse
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateRequest
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateRequestData
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import java.util.logging.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RemoteScheduleRepository(private val context: Context) : ScheduleRepository {
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getMainPage(): MyResult<String> {
        return withContext(Dispatchers.IO) {
            val mainPageFuture = RequestFuture.newFuture<String>()
            val request = StringRequest(
                Request.Method.GET,
                "https://politehnikum-eng.ru/index/raspisanie_zanjatij/0-409",
                mainPageFuture,
                mainPageFuture
            )
            NetworkConnection.getInstance(context).addToRequestQueue(request)

            try {
                val encodedMainPage =
                    Base64.Default.encode(mainPageFuture.get().encodeToByteArray())
                MyResult.Success(encodedMainPage)
            } catch (exception: Exception) {
                MyResult.Failure(exception)
            }
        }
    }

    suspend fun updateMainPage(): MyResult<Nothing> {
        return withContext(Dispatchers.IO) {
            val mainPage = getMainPage()

            if (mainPage is MyResult.Failure)
                return@withContext mainPage

            val updateFuture = RequestFuture.newFuture<Nothing>()
            ScheduleUpdateRequest(
                ScheduleUpdateRequestData((mainPage as MyResult.Success<String>).data),
                context,
                updateFuture,
                updateFuture
            ).send()

            try {
                MyResult.Success(updateFuture.get())
            } catch (exception: Exception) {
                MyResult.Failure(exception)
            }
        }
    }

    override suspend fun getGroup(): MyResult<Group> {
        return withContext(Dispatchers.IO) {
            val logger = Logger.getLogger("RemoteScheduleRepository")

            val groupName = runBlocking {
                context.settingsDataStore.data.map { settings -> settings.group }.first()
            }

            if (groupName.isEmpty())
                return@withContext MyResult.Failure(IllegalArgumentException("No group name provided!"))

            val firstPassFuture = RequestFuture.newFuture<ScheduleGetResponse>()
            ScheduleGetRequest(
                ScheduleGetRequestData(groupName),
                context,
                firstPassFuture,
                firstPassFuture
            ).send()

            var firstPassResponse: ScheduleGetResponse? = null

            try {
                firstPassResponse = firstPassFuture.get()
                if (!firstPassResponse.updateRequired) {
                    logger.info("Successfully get group schedule!")
                    return@withContext MyResult.Success(firstPassFuture.get().group)
                }
                logger.info("Successfully get group schedule, but it needs to update!")
            } catch (exception: Exception) {
                if (exception.cause !is ServerError)
                    return@withContext MyResult.Failure(exception)
                logger.info("Can't get group schedule, because it needs to first update!")
            }

            val updateResult = updateMainPage()
            if (updateResult is MyResult.Failure) {
                logger.info("Can't update site main page!")
                if (firstPassResponse != null)
                    return@withContext MyResult.Success(firstPassResponse.group)

                return@withContext updateResult
            }
            logger.info("Site main page successfully updated!")

            val secondPassFuture = RequestFuture.newFuture<ScheduleGetResponse>()
            ScheduleGetRequest(
                ScheduleGetRequestData(groupName),
                context,
                secondPassFuture,
                secondPassFuture
            ).send()

            try {
                MyResult.Success(secondPassFuture.get().group)
            } catch (exception: Exception) {
                MyResult.Failure(exception)
            }
        }
    }
}