package ru.n08i40k.polytechnic.next.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.n08i40k.polytechnic.next.work.ScheduleClvAlarm
import java.util.logging.Logger

class BootCompletedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val logger = Logger.getLogger("BootCompletedBroadcastReceiver")

        if (context == null) {
            logger.warning("No context provided!")
            return
        }

        if (intent == null) {
            logger.warning("No intend provided!")
            return
        }

        if (intent.action != "android.intent.action.BOOT_COMPLETED") {
            logger.warning("Strange intent action passed!")
            logger.warning(intent.action)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ScheduleClvAlarm>()
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(request)
    }
}