package ru.n08i40k.polytechnic.next.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.data.MyResult

class ScheduleClvAlarm(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val application = applicationContext as PolytechnicApplication

        val result = runBlocking {
            application
                .container
                .scheduleRepository
                .getGroup()
        }

        if (result is MyResult.Failure)
            return Result.failure()

        application.scheduleClvService((result as MyResult.Success).data)

        return Result.success()
    }
}