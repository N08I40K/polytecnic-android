package ru.n08i40k.polytechnic.next

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp
import ru.n08i40k.polytechnic.next.data.AppContainer
import javax.inject.Inject

@HiltAndroidApp
class PolytechnicApplication : Application() {
    @Inject
    lateinit var container: AppContainer

    fun getAppVersion(): String {
        return applicationContext.packageManager
            .getPackageInfo(this.packageName, 0)
            .versionName!!
    }

    fun hasNotificationPermission(): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)
    }
}