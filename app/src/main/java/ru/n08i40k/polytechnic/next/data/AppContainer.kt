package ru.n08i40k.polytechnic.next.data

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.n08i40k.polytechnic.next.data.cache.NetworkCacheRepository
import ru.n08i40k.polytechnic.next.data.cache.impl.FakeNetworkCacheRepository
import ru.n08i40k.polytechnic.next.data.cache.impl.LocalNetworkCacheRepository
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.data.schedule.impl.RemoteScheduleRepository
import ru.n08i40k.polytechnic.next.data.scheduleReplacer.ScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.data.scheduleReplacer.impl.FakeScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.data.scheduleReplacer.impl.RemoteScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.RemoteProfileRepository
import javax.inject.Singleton

interface AppContainer {
    val applicationContext: Context

    val networkCacheRepository: NetworkCacheRepository

    val scheduleRepository: ScheduleRepository

    val scheduleReplacerRepository: ScheduleReplacerRepository

    val profileRepository: ProfileRepository

    val remoteConfig: FirebaseRemoteConfig
}

class MockAppContainer(override val applicationContext: Context) : AppContainer {
    override val networkCacheRepository: NetworkCacheRepository
            by lazy { FakeNetworkCacheRepository() }

    override val scheduleRepository: ScheduleRepository
            by lazy { FakeScheduleRepository() }

    override val scheduleReplacerRepository: ScheduleReplacerRepository
            by lazy { FakeScheduleReplacerRepository() }

    override val profileRepository: ProfileRepository
            by lazy { FakeProfileRepository() }

    override val remoteConfig: FirebaseRemoteConfig
            by lazy { Firebase.remoteConfig }
}

class RemoteAppContainer(override val applicationContext: Context) : AppContainer {
    override val networkCacheRepository: NetworkCacheRepository
            by lazy { LocalNetworkCacheRepository(applicationContext) }

    override val scheduleRepository: ScheduleRepository
            by lazy { RemoteScheduleRepository(applicationContext) }

    override val scheduleReplacerRepository: ScheduleReplacerRepository
            by lazy { RemoteScheduleReplacerRepository(applicationContext) }

    override val profileRepository: ProfileRepository
            by lazy { RemoteProfileRepository(applicationContext) }

    override val remoteConfig: FirebaseRemoteConfig
            by lazy { Firebase.remoteConfig }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppContainer(application: Application): AppContainer {
        return RemoteAppContainer(application.applicationContext)
    }
}