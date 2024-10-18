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
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock
import ru.n08i40k.polytechnic.next.utils.getDayMinutes
import java.util.Calendar
import java.util.logging.Logger

@Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
class CurrentLessonViewService : Service() {
    companion object {
        private const val NOTIFICATION_STATUS_ID = 1337
        private const val NOTIFICATION_END_ID = NOTIFICATION_STATUS_ID + 1
        private const val UPDATE_INTERVAL = 60_000L

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

    private var day: Day? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            val logger = Logger.getLogger("CLV.updateRunnable")

            if (day == null || day!!.lessons.isEmpty()) {
                logger.warning("Stopping, because day is null or empty!")
                stopSelf()
                return
            }

            val currentMinutes = Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                .get(Calendar.MINUTE)

            val currentLessonEntry = day!!.currentKV
            val currentLessonIdx: Int? = currentLessonEntry?.first
            val currentLesson: Lesson? = currentLessonEntry?.second

            val nextLessonEntry = day!!.distanceToNextByIdx(currentLessonIdx)
            val nextLesson =
                if (nextLessonEntry == null)
                    null
                else
                    day!!.lessons[nextLessonEntry.first]

            if (currentLesson == null && nextLesson == null) {
                val notification = NotificationCompat
                    .Builder(applicationContext, NotificationChannels.LESSON_VIEW)
                    .setSmallIcon(R.drawable.schedule)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(getString(R.string.lessons_end_notification_title))
                    .setContentText(getString(R.string.lessons_end_notification_description))
                    .build()
                getNotificationManager().notify(NOTIFICATION_END_ID, notification)

                stopSelf()
                return
            }

            val firstLessonIdx =
                day!!.distanceToNextByLocalDateTime(LocalDateTime(0, 0, 0, 0, 0))?.first
                    ?: throw NullPointerException("Is this even real?")
            val distanceToFirst = day!!.lessons[firstLessonIdx]!!.time!!.start.dayMinutes - currentMinutes

            val currentLessonDelay =
                if (currentLesson == null) // Если эта пара - перемена, то конец перемены через (результат getDistanceToNext)
                    nextLessonEntry!!.second
                else // Если эта пара - обычная пара, то конец пары через (конец этой пары - текущее кол-во минут)
                    currentLesson.time!!.end.dayMinutes - currentMinutes

            val currentLessonName =
                currentLesson?.getNameAndCabinetsShort(this@CurrentLessonViewService)
                    ?: run {
                        if (distanceToFirst > 0)
                            getString(R.string.lessons_not_started)
                        else
                            getString(R.string.lesson_break)
                    }

            val nextLessonName =
                if (currentLesson == null) // Если текущая пара - перемена
                    nextLesson!!.getNameAndCabinetsShort(this@CurrentLessonViewService)
                else if (nextLesson == null) // Если текущая пара - последняя
                    getString(R.string.lessons_end)
                else // Если после текущей пары есть ещё пара(ы)
                    getString(R.string.lesson_break)

            val nextLessonTotal =
                if (currentLesson == null)
                    nextLesson!!.time!!.start
                else
                    currentLesson.time!!.end

            val notification = createNotification(
                getString(
                    if (distanceToFirst > 0)
                        R.string.waiting_for_day_start_notification_title
                    else
                        R.string.lesson_going_notification_title,
                    currentLessonDelay / 60,
                    currentLessonDelay % 60
                ),
                getString(
                    R.string.lesson_going_notification_description,
                    currentLessonName,
                    nextLessonTotal.dayMinutes.fmtAsClock(),
                    nextLessonName,
                )
            )
            getNotificationManager().notify(NOTIFICATION_STATUS_ID, notification)

            handler.postDelayed(this, UPDATE_INTERVAL)
        }
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

    private fun updateSchedule(group: Group?) {
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
                intent.getParcelableExtra("group", Group::class.java)
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