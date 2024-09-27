package ru.n08i40k.polytechnic.next.network.data

import android.content.Context
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.network.NetworkConnection
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetCacheStatusRequest
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetCacheStatusResponse
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateRequest
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateRequestData
import java.util.logging.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

open class CachedRequest(
    context: Context,
    method: Int,
    private val url: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(context, method, url, {
    runBlocking {
        (context as PolytechnicApplication)
            .container.networkCacheRepository.put(url, it)
    }
    listener.onResponse(it)
}, errorListener) {
    private val appContainer: AppContainer = (context as PolytechnicApplication).container

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getMainPage(): MyResult<String> {
        return withContext(Dispatchers.IO) {
            val mainPageFuture = RequestFuture.newFuture<String>()
            val request = StringRequest(
                Method.GET,
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

    private suspend fun updateMainPage(): MyResult<ScheduleGetCacheStatusResponse> {
        return withContext(Dispatchers.IO) {
            val mainPage = getMainPage()

            if (mainPage is MyResult.Failure)
                return@withContext mainPage

            val updateFuture = RequestFuture.newFuture<ScheduleGetCacheStatusResponse>()
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

    override fun send() {
        val logger = Logger.getLogger("CachedRequest")

        val repository = appContainer.networkCacheRepository

        val future = RequestFuture.newFuture<ScheduleGetCacheStatusResponse>()

        logger.info("Getting cache status...")
        ScheduleGetCacheStatusRequest(context, future, future).send()

        try {
            val response = future.get()

            logger.info("Cache status received successfully!")

            if (!response.cacheUpdateRequired) {
                logger.info("Cache update was not required!")
                runBlocking {
                    repository.setUpdateDates(response.lastCacheUpdate, response.lastScheduleUpdate)
                    repository.setHash(response.cacheHash)
                }
            } else {
                logger.info("Cache update was required!")
                val updateResult = runBlocking { updateMainPage() }

                when (updateResult) {
                    is MyResult.Success -> {
                        logger.info("Cache update was successful!")
                        runBlocking {
                            repository.setUpdateDates(
                                updateResult.data.lastCacheUpdate,
                                updateResult.data.lastScheduleUpdate
                            )
                            repository.setHash(updateResult.data.cacheHash)
                        }
                    }

                    is MyResult.Failure -> {
                        logger.warning("Failed to update cache!")
                        super.getErrorListener()
                            ?.onErrorResponse(updateResult.exception.cause as VolleyError)
                        return
                    }
                }
            }
        } catch (exception: Exception) {
            logger.warning("Failed to get cache status!")
            super.getErrorListener()?.onErrorResponse(exception.cause as VolleyError)
            return
        }

        val cachedResponse = runBlocking { repository.get(url) }
        if (cachedResponse != null) {
            logger.info("Found cached response!")
            listener.onResponse(cachedResponse.data)
            return
        }

        logger.info("Cached response doesn't exists!")
        super.send()
    }
}