package ru.n08i40k.polytechnic.next.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.NotificationChannels
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import java.time.Duration

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SetFcmTokenWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
            .setInputData(workDataOf("TOKEN" to token))
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueue(request)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]

        when (type) {
            "schedule-update" -> {
                val notification = NotificationCompat
                    .Builder(applicationContext, NotificationChannels.SCHEDULE_UPDATE)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(getString(R.string.schedule_update_title))
                    .setContentText(
                        getString(
                            if (message.data["replaced"] == "true")
                                R.string.schedule_update_replaced
                            else
                                R.string.schedule_update_default
                        )
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                with(NotificationManagerCompat.from(this)) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MyFirebaseMessagingService,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@with
                    }

                    notify(message.data["etag"].hashCode(), notification)
                }
            }
        }

        super.onMessageReceived(message)
    }

    class SetFcmTokenWorker(context: Context, workerParams: WorkerParameters) :
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
}