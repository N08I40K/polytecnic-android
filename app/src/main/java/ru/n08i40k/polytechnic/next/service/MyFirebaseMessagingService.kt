package ru.n08i40k.polytechnic.next.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.NotificationChannels
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.work.FcmSetTokenWorker
import java.time.Duration

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<FcmSetTokenWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
            .setInputData(workDataOf("TOKEN" to token))
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueue(request)
    }

    private fun sendNotification(
        channel: String,
        @DrawableRes iconId: Int,
        title: String,
        contentText: String,
        priority: Int,
        id: Any?,
        intent: Intent? = null
    ) {
        val pendingIntent: PendingIntent? =
            if (intent != null)
                PendingIntent.getActivity(this, 0, intent.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }, PendingIntent.FLAG_IMMUTABLE)
            else
                null

        val notification = NotificationCompat
            .Builder(applicationContext, channel)
            .setSmallIcon(iconId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }

            notify(id.hashCode(), notification)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]

        when (type) {
            "schedule-update" -> {
                sendNotification(
                    NotificationChannels.SCHEDULE_UPDATE,
                    R.drawable.schedule,
                    getString(R.string.schedule_update_title),
                    getString(
                        if (message.data["replaced"] == "true")
                            R.string.schedule_update_replaced
                        else
                            R.string.schedule_update_default
                    ),
                    NotificationCompat.PRIORITY_DEFAULT,
                    message.data["etag"]
                )
            }

            "lessons-start" -> {
                scope.launch {
                    CurrentLessonViewService
                        .startService(applicationContext as PolytechnicApplication)
                }
            }

            "app-update" -> {
                sendNotification(
                    NotificationChannels.APP_UPDATE,
                    R.drawable.download,
                    getString(R.string.app_update_title, message.data["version"]),
                    getString(R.string.app_update_description),
                    NotificationCompat.PRIORITY_DEFAULT,
                    message.data["version"],
                    Intent(Intent.ACTION_VIEW, Uri.parse(message.data["downloadLink"]))
                )
            }
        }

        super.onMessageReceived(message)
    }
}