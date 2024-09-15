package ru.n08i40k.polytechnic.next

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.n08i40k.polytechnic.next.data.AppContainer
import javax.inject.Inject

@HiltAndroidApp
class PolytechnicApplication : Application() {
    @Suppress("unused")
    @Inject
    lateinit var container: AppContainer
}