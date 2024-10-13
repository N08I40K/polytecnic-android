package ru.n08i40k.polytechnic.next.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.NotificationChannels
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.work.FcmUpdateCallbackWorker
import ru.n08i40k.polytechnic.next.work.LinkUpdateWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        name: String,
        description: String,
        channelId: String
    ) {
        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = description

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotificationChannels() {
        if (!(applicationContext as PolytechnicApplication).hasNotificationPermission())
            return

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager,
            getString(R.string.schedule_channel_name),
            getString(R.string.schedule_channel_description),
            NotificationChannels.SCHEDULE_UPDATE
        )

        createNotificationChannel(
            notificationManager,
            getString(R.string.app_update_channel_name),
            getString(R.string.app_update_channel_description),
            NotificationChannels.APP_UPDATE
        )

        createNotificationChannel(
            notificationManager,
            getString(R.string.lesson_view_channel_name),
            getString(R.string.lesson_view_channel_description),
            NotificationChannels.LESSON_VIEW
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) createNotificationChannels()
        }

    private fun askNotificationPermission() {
        if (!(applicationContext as PolytechnicApplication).hasNotificationPermission())
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


    fun scheduleLinkUpdate(intervalInMinutes: Long) {
        val tag = "schedule-update"

        val workRequest = PeriodicWorkRequest.Builder(
            LinkUpdateWorker::class.java,
            intervalInMinutes.coerceAtLeast(15), TimeUnit.MINUTES
        )
            .addTag(tag)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        workManager.cancelAllWorkByTag(tag)
        workManager.enqueue(workRequest)
    }

    private fun setupFirebaseConfig() {
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.w("RemoteConfig", "Failed to fetch and activate!")

                scheduleLinkUpdate(remoteConfig.getLong("linkUpdateDelay"))
            }
    }

    private fun handleUpdate() {
        lifecycleScope.launch {
            val appVersion = (applicationContext as PolytechnicApplication).getAppVersion()

            if (settingsDataStore.data.map { it.version }.first() != appVersion) {
                settingsDataStore.updateData { it.toBuilder().setVersion(appVersion).build() }

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val request = OneTimeWorkRequestBuilder<FcmUpdateCallbackWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                    .setInputData(workDataOf("VERSION" to appVersion))
                    .build()

                WorkManager
                    .getInstance(this@MainActivity)
                    .enqueue(request)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        createNotificationChannels()

        setupFirebaseConfig()

        handleUpdate()

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