package ru.n08i40k.polytechnic.next.network.request

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
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetCacheStatus
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleUpdate
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

    private suspend fun updateMainPage(): MyResult<ScheduleGetCacheStatus.ResponseDto> {
        return withContext(Dispatchers.IO) {
            when (val mainPage = getMainPage()) {
                is MyResult.Failure -> mainPage
                is MyResult.Success -> {
                    tryFuture {
                        ScheduleUpdate(
                            ScheduleUpdate.RequestDto(mainPage.data),
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

        val cacheStatusResult = tryFuture {
            ScheduleGetCacheStatus(context, it, it)
        }

        if (cacheStatusResult is MyResult.Success) {
            val cacheStatus = cacheStatusResult.data

            runBlocking {
                repository.setUpdateDates(
                    cacheStatus.lastCacheUpdate,
                    cacheStatus.lastScheduleUpdate
                )
                repository.setHash(cacheStatus.cacheHash)
            }

            if (cacheStatus.cacheUpdateRequired) {
                val updateResult = runBlocking { updateMainPage() }

                when (updateResult) {
                    is MyResult.Success -> {
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
            listener.onResponse(cachedResponse.data)
            return
        }

        super.send()
    }
}