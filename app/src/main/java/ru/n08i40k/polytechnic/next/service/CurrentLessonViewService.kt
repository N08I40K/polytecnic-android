package ru.n08i40k.polytechnic.next.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.NotificationChannels
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock
import ru.n08i40k.polytechnic.next.utils.getDayMinutes
import ru.n08i40k.polytechnic.next.utils.now
import java.util.Calendar
import java.util.logging.Logger

class CurrentLessonViewService : Service() {
    companion object {
        private const val NOTIFICATION_STATUS_ID = 1337
        private const val NOTIFICATION_END_ID = NOTIFICATION_STATUS_ID + 1
        private const val UPDATE_INTERVAL = 1_000L

        suspend fun startService(application: PolytechnicApplication) {
            if (!application.hasNotificationPermission())
                return

            val schedule =
                application
                    .container
                    .scheduleRepository
                    .getGroup()

            if (schedule is MyResult.Failure)
                return

            val intent = Intent(application, CurrentLessonViewService::class.java)
                .apply {
                    putExtra("group", (schedule as MyResult.Success).data)
                }

            application.stopService(
                Intent(
                    application,
                    CurrentLessonViewService::class.java
                )
            )
            startForegroundService(application, intent)
        }
    }

    private lateinit var day: Day

    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            val (currentIndex, currentLesson) = day.currentKV ?: (null to null)
            val (nextIndex, _) = day.distanceToNext(currentIndex)
                ?: (null to null)

            val nextLesson = nextIndex?.let { day.lessons[nextIndex] }

            if (currentLesson == null && nextLesson == null) {
                onLessonsEnd()
                return
            }

            handler.postDelayed(this, UPDATE_INTERVAL)

            val context = this@CurrentLessonViewService
            val currentMinutes = LocalDateTime.now().dayMinutes

            val distanceToFirst = day.first!!.time.start.dayMinutes - currentMinutes

            val currentLessonName =
                currentLesson?.getNameAndCabinetsShort(context)
                    ?: run {
                        if (distanceToFirst > 0)
                            getString(R.string.lessons_not_started)
                        else
                            getString(R.string.lesson_break)
                    }

            val nextLessonName =
                nextLesson?.getNameAndCabinetsShort(context) ?: getString(R.string.lessons_end)

            val nextLessonIn =
                (currentLesson?.time?.end ?: nextLesson!!.time.start).dayMinutes

            val notification = createNotification(
                getString(
                    if (distanceToFirst > 0) R.string.waiting_for_day_start_notification_title
                    else R.string.lesson_going_notification_title,
                    (nextLessonIn - currentMinutes) / 60,
                    (nextLessonIn - currentMinutes) % 60
                ),
                getString(
                    R.string.lesson_going_notification_description,
                    currentLessonName,
                    nextLessonIn.fmtAsClock(),
                    nextLessonName,
                )
            )
            getNotificationManager().notify(NOTIFICATION_STATUS_ID, notification)
        }
    }

    private fun onLessonsEnd() {
        val notification = NotificationCompat
            .Builder(applicationContext, NotificationChannels.LESSON_VIEW)
            .setSmallIcon(R.drawable.schedule)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(getString(R.string.lessons_end_notification_title))
            .setContentText(getString(R.string.lessons_end_notification_description))
            .build()
        getNotificationManager().notify(NOTIFICATION_END_ID, notification)

        stopSelf()
    }

    private fun createNotification(
        title: String? = null,
        description: String? = null
    ): Notification {
        return NotificationCompat
            .Builder(applicationContext, NotificationChannels.LESSON_VIEW)
            .setSmallIcon(R.drawable.schedule)
            .setContentTitle(title ?: getString(R.string.lesson_notification_title))
            .setContentText(description ?: getString(R.string.lesson_notification_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun updateSchedule(group: GroupOrTeacher?) {
        val logger = Logger.getLogger("CLV")

        if (group == null) {
            logger.warning("Stopping, because group is null")
            stopSelf()
            return
        }

        val currentDay = group.current
        if (currentDay == null || currentDay.lessons.isEmpty()) {
            logger.warning("Stopping, because current day is null or empty")
            stopSelf()
            return
        }

        val nowMinutes = Calendar.getInstance().getDayMinutes()
        if (nowMinutes < ((5 * 60) + 30)
            || currentDay.last!!.time.end.dayMinutes < nowMinutes
        ) {
            logger.warning("Stopping, because service started outside of acceptable time range!")
            stopSelf()
            return
        }

        this.day = currentDay

        this.handler.removeCallbacks(updateRunnable)
        updateRunnable.run()

        logger.info("Running...")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!(applicationContext as PolytechnicApplication).hasNotificationPermission()) {
            stopSelf()
            return START_STICKY
        }

        if (intent == null)
            throw NullPointerException("Intent shouldn't be null!")

        val notification = createNotification()
        startForeground(NOTIFICATION_STATUS_ID, notification)

        updateSchedule(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("group", GroupOrTeacher::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("group")
            }
        )

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}