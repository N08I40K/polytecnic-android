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
import java.util.regex.Pattern

open class CachedRequest(
    context: Context,
    method: Int,
    private val url: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(context, method, url, {
    runBlocking(Dispatchers.IO) {
        (context as PolytechnicApplication)
            .container.networkCacheRepository.put(url, it)
    }
    listener.onResponse(it)
}, errorListener) {
    private val appContainer: AppContainer = (context as PolytechnicApplication).container

    private suspend fun getXlsUrl(): MyResult<String> = withContext(Dispatchers.IO) {
        val mainPageFuture = RequestFuture.newFuture<String>()
        val request = StringRequest(
            Method.GET,
            "https://politehnikum-eng.ru/index/raspisanie_zanjatij/0-409",
            mainPageFuture,
            mainPageFuture
        )
        NetworkConnection.getInstance(context).addToRequestQueue(request)

        val response = tryGet(mainPageFuture)
        if (response is MyResult.Failure)
            return@withContext response

        val pageData = (response as MyResult.Success).data

        val remoteConfig = (context.applicationContext as PolytechnicApplication).container.remoteConfig

        val pattern: Pattern =
            Pattern.compile(remoteConfig.getString("linkParserRegex"), Pattern.MULTILINE)

        val matcher = pattern.matcher(pageData)
        if (!matcher.find())
            return@withContext MyResult.Failure(RuntimeException("Required url not found!"))

        MyResult.Success("https://politehnikum-eng.ru" + matcher.group(1))
    }


    private suspend fun updateMainPage(): MyResult<ScheduleGetCacheStatus.ResponseDto> {
        return withContext(Dispatchers.IO) {
            when (val xlsUrl = getXlsUrl()) {
                is MyResult.Failure -> xlsUrl
                is MyResult.Success -> {
                    tryFuture {
                        ScheduleUpdate(
                            ScheduleUpdate.RequestDto(xlsUrl.data),
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