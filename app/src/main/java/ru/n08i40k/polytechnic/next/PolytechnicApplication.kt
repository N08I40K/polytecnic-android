package ru.n08i40k.polytechnic.next

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.utils.or
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
}