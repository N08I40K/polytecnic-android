package ru.n08i40k.polytechnic.next.network.data

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.network.NetworkConnection
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetCacheStatusReq
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetCacheStatusResData
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateReq
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleUpdateReqData
import ru.n08i40k.polytechnic.next.network.tryFuture
import ru.n08i40k.polytechnic.next.network.tryGet
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

            when (val response = tryGet(mainPageFuture)) {
                is MyResult.Failure -> response
                is MyResult.Success -> {
                    val encodedMainPage = Base64.Default.encode(response.data.encodeToByteArray())
                    MyResult.Success(encodedMainPage)
                }
            }
        }
    }

    private suspend fun updateMainPage(): MyResult<ScheduleGetCacheStatusResData> {
        return withContext(Dispatchers.IO) {
            when (val mainPage = getMainPage()) {
                is MyResult.Failure -> mainPage
                is MyResult.Success -> {
                    tryFuture {
                        ScheduleUpdateReq(
                            ScheduleUpdateReqData(mainPage.data),
                            context,
                            it,
                            it
                        )
                    }
                }
            }
        }
    }

    override fun send() {
        val logger = Logger.getLogger("CachedRequest")
        val repository = appContainer.networkCacheRepository

        logger.info("Getting cache status...")

        val cacheStatusResult = tryFuture {
            ScheduleGetCacheStatusReq(context, it, it)
        }

        if (cacheStatusResult is MyResult.Success) {
            val cacheStatus = cacheStatusResult.data

            logger.info("Cache status received successfully!")

            runBlocking {
                repository.setUpdateDates(
                    cacheStatus.lastCacheUpdate,
                    cacheStatus.lastScheduleUpdate
                )
                repository.setHash(cacheStatus.cacheHash)
            }

            if (cacheStatus.cacheUpdateRequired) {
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
                    }
                }
            }
        } else {
            logger.warning("Failed to get cache status!")
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