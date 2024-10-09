package ru.n08i40k.polytechnic.next

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.receiver.AlarmReceiver
import ru.n08i40k.polytechnic.next.utils.or
import java.util.Calendar
import javax.inject.Inject

@HiltAndroidApp
class PolytechnicApplication : Application() {
    @Suppress("unused")
    @Inject
    lateinit var container: AppContainer

    fun getAppVersion(): String {
        return applicationContext.packageManager
            .getPackageInfo(this.packageName, 0)
            .versionName or "1.0.0"
    }

    fun hasNotificationPermission(): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun getDate(group: Group): Calendar? {
        val javaCalendar = Calendar.getInstance()
        val currentMinutes = javaCalendar.get(Calendar.HOUR_OF_DAY) * 60 +
                javaCalendar.get(Calendar.MINUTE)
        var startDayIdx = javaCalendar.get(Calendar.DAY_OF_WEEK) - 2

        println("Current day is $startDayIdx")

        val currentDay = group.days[startDayIdx]
        if (currentDay != null) {
            val firstLesson = currentDay.first

            if (firstLesson == null || firstLesson.time.start < currentMinutes) {
                println("Current day already started or ended!")
                ++startDayIdx
            }
        }

        for (dayIdx in startDayIdx..5) {
            println("Trying $dayIdx day...")
            val day = group.days[dayIdx] ?: continue
            println("Day isn't null")
            val firstLesson = day.first ?: continue
            println("Day isn't empty")

            val executeMinutes = (firstLesson.time.start - 15).coerceAtLeast(0)

            println("Schedule minutes at $executeMinutes")

            return Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, dayIdx + 2) // sunday is first + index from 0
                set(Calendar.HOUR_OF_DAY, executeMinutes / 60)
                set(Calendar.MINUTE, executeMinutes % 60)
                set(Calendar.SECOND, 0)
//                set(Calendar.MINUTE, get(Calendar.MINUTE) + 1)
            }
        }

        return null
    }

    fun scheduleClvService(group: Group) {
        // -1 = вс
        //  0 = пн
        //  1 = вт
        //  2 = ср
        //  3 = чт
        //  4 = пт
        //  5 = сб

        println("Getting date...")

        val date = getDate(group) ?: return

        println("Alarm on this week!")

        val alarmManager = applicationContext
            .getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val pendingIntent =
            Intent(applicationContext, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(
                    applicationContext,
                    IntentRequestCodes.ALARM_CLV,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        if (alarmManager != null)
            println("Alarm manager isn't null.")

        alarmManager?.cancel(pendingIntent)
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            date.timeInMillis,
            pendingIntent
        )
    }
}