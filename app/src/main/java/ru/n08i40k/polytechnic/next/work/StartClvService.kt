package ru.n08i40k.polytechnic.next.work

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.service.CurrentLessonViewService

class StartClvService(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val schedule = runBlocking {
            (applicationContext as PolytechnicApplication)
                .container
                .scheduleRepository
                .getGroup()
        }

        if (schedule is MyResult.Failure)
            return Result.success()

        val intent = Intent(applicationContext, CurrentLessonViewService::class.java)
            .apply {
                putExtra("group", (schedule as MyResult.Success).data)
            }

        applicationContext.stopService(
            Intent(
                applicationContext,
                CurrentLessonViewService::class.java
            )
        )
        startForegroundService(applicationContext, intent)

        return Result.success()
    }
}