package ru.n08i40k.polytechnic.next.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.PolytechnicApplication

class LinkUpdateWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        runBlocking {
            (applicationContext as PolytechnicApplication)
                .container
                .scheduleRepository
                .getGroup()
        }

//        CurrentLessonViewService.startService(applicationContext)

        return Result.success()
    }
}