package ru.n08i40k.polytechnic.next.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.n08i40k.polytechnic.next.NotificationChannels
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.utils.fmtAsClock
import ru.n08i40k.polytechnic.next.work.StartClvService
import java.util.Calendar

class CurrentLessonViewService : Service() {
    companion object {
        private const val NOTIFICATION_STATUS_ID = 1337
        private const val NOTIFICATION_END_ID = NOTIFICATION_STATUS_ID + 1
        private const val UPDATE_INTERVAL = 60_000L

        fun startService(appContext: Context) {
            if (!(appContext as PolytechnicApplication).hasNotificationPermission())
                return

            if (Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                    .get(Calendar.MINUTE) < 420)
                return

            val request = OneTimeWorkRequestBuilder<StartClvService>()
                .build()

            WorkManager.getInstance(appContext).enqueue(request)
        }
    }

    private var day: Day? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (day == null || day!!.nonNullIndices.isEmpty()) {
                stopSelf()
                return
            }

            val currentMinutes = Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                .get(Calendar.MINUTE)

            val currentLessonEntry = day!!.getCurrentLesson()
            val currentLessonIdx: Int? = currentLessonEntry?.key
            val currentLesson: Lesson? = currentLessonEntry?.value

            val nextLessonEntry = day!!.getDistanceToNextByIdx(currentLessonIdx)
            val nextLesson =
                if (nextLessonEntry == null)
                    null
                else
                    day!!.lessons[nextLessonEntry.key]

            if (currentLesson == null && nextLesson == null) {
                val notification = NotificationCompat
                    .Builder(applicationContext, NotificationChannels.LESSON_VIEW)
                    .setSmallIcon(R.drawable.logo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(getString(R.string.lessons_end_notification_title))
                    .setContentText(getString(R.string.lessons_end_notification_description))
                    .build()
                getNotificationManager().notify(NOTIFICATION_END_ID, notification)

                stopSelf()
                return
            }

            val firstLessonIdx = day!!.getDistanceToNextByMinutes(0)?.key
                ?: throw NullPointerException("Is this even real?")
            val distanceToFirst = day!!.lessons[firstLessonIdx]!!.time!!.start - currentMinutes

            val currentLessonDelay =
                if (currentLesson == null) // Если эта пара - перемена, то конец перемены через (результат getDistanceToNext)
                    nextLessonEntry!!.value
                else // Если эта пара - обычная пара, то конец пары через (конец этой пары - текущее кол-во минут)
                    currentLesson.time!!.end - currentMinutes

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
                    nextLessonTotal.fmtAsClock(),
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
            .setSmallIcon(R.drawable.logo)
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

    fun updateSchedule(group: Group?): Boolean {
        if (group == null) {
            stopSelf()
            return false
        }

        val day = group.getCurrentDay()
        if (day?.value == null) {
            stopSelf()
            return false
        }

        val dayValue = day.value!!

        if (this.day == null) {
            if (dayValue.lessons[dayValue.defaultIndices[dayValue.defaultIndices.lastIndex]]!!.time!!.end
                <= Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                    .get(Calendar.MINUTE)
            ) {
                stopSelf()
                return false
            }
        }

        this.day = dayValue

        this.handler.removeCallbacks(updateRunnable)
        updateRunnable.run()

        return true
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

        if (!updateSchedule(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("group", Group::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("group")
                }
            )
        )
            updateRunnable.run()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}