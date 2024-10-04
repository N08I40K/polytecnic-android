package ru.n08i40k.polytechnic.next.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.NotificationChannels.SCHEDULE_UPDATE
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.schedule_channel_name)
            val description = getString(R.string.schedule_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(SCHEDULE_UPDATE, name, importance)
            channel.description = description

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            createNotificationChannel()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun askNotificationPermission() {
        if (!hasNotificationPermission())
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    class CacheUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            runBlocking {
                (applicationContext as PolytechnicApplication)
                    .container
                    .scheduleRepository
                    .getGroup()
            }
            return Result.success()
        }
    }

    private fun schedulePeriodicRequest() {
        val workRequest = PeriodicWorkRequest.Builder(
            CacheUpdateWorker::class.java,
            15, TimeUnit.MINUTES
        )
            .addTag("schedule-update")
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        workManager.cancelAllWorkByTag("schedule-update")
        workManager.enqueue(workRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        schedulePeriodicRequest()
        askNotificationPermission()

        if (hasNotificationPermission())
            createNotificationChannel()

        setContent {
            Box(Modifier.windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))) {
                PolytechnicApp()
            }
        }

        lifecycleScope.launch {
            settingsDataStore.data.first()
        }
    }
}