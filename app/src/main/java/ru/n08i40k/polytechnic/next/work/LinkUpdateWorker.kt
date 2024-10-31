package ru.n08i40k.polytechnic.next.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.settings.settingsDataStore

class LinkUpdateWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val accessToken = runBlocking {
            applicationContext.settingsDataStore.data.map { it.accessToken }.first()
        }
        if (accessToken.isEmpty())
            return Result.retry()

        runBlocking {
            (applicationContext as PolytechnicApplication)
                .container
                .scheduleRepository
                .getGroup()
        }

        return Result.success()
    }
}