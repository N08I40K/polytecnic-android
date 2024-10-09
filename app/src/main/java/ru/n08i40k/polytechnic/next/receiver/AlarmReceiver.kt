package ru.n08i40k.polytechnic.next.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.n08i40k.polytechnic.next.service.CurrentLessonViewService
import ru.n08i40k.polytechnic.next.work.ScheduleClvAlarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Hi from AlarmReceiver")

        if (intent == null) {
            println("No intend provided!")
            return
        }

        if (context == null) {
            println("No context provided!")
            return
        }
        println(intent.action)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val rescheduleRequest = OneTimeWorkRequestBuilder<ScheduleClvAlarm>()
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(rescheduleRequest)

        CurrentLessonViewService.startService(context.applicationContext)
    }
}