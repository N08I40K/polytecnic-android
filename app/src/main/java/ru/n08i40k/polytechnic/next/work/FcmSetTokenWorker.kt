package ru.n08i40k.polytechnic.next.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.settings.settingsDataStore

class FcmSetTokenWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val fcmToken = inputData.getString("TOKEN") ?: return Result.failure()

        val accessToken = runBlocking {
            applicationContext.settingsDataStore.data.map { it.accessToken }.first()
        }
        if (accessToken.isEmpty())
            return Result.retry()

        val setResult = runBlocking {
            (applicationContext as PolytechnicApplication)
                .container
                .profileRepository
                .setFcmToken(fcmToken)
        }

        return when (setResult) {
            is MyResult.Success -> Result.success()
            is MyResult.Failure -> Result.retry()
        }
    }
}