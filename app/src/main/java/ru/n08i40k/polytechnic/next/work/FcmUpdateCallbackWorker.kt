package ru.n08i40k.polytechnic.next.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.network.request.fcm.FcmUpdateCallback
import ru.n08i40k.polytechnic.next.network.tryFuture
import ru.n08i40k.polytechnic.next.settings.settingsDataStore

class FcmUpdateCallbackWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val version = inputData.getString("VERSION") ?: return Result.failure()

        val accessToken = runBlocking {
            applicationContext.settingsDataStore.data.map { it.accessToken }.first()
        }
        if (accessToken.isEmpty())
            return Result.retry()

        val result = runBlocking {
            tryFuture {
                FcmUpdateCallback(this@FcmUpdateCallbackWorker.applicationContext, version, it, it)
            }
        }

        return when (result) {
            is MyResult.Success -> Result.success()
            is MyResult.Failure -> Result.retry()
        }
    }
}