package ru.n08i40k.polytechnic.next.data

import android.app.Application
import android.content.Context
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
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.RemoteProfileRepository
import javax.inject.Singleton

interface AppContainer {
    val applicationContext: Context
    val networkCacheRepository: NetworkCacheRepository
    val scheduleRepository: ScheduleRepository
    val profileRepository: ProfileRepository
}

class MockAppContainer(override val applicationContext: Context) : AppContainer {
    override val networkCacheRepository: NetworkCacheRepository by lazy { FakeNetworkCacheRepository() }
    override val scheduleRepository: ScheduleRepository by lazy { FakeScheduleRepository() }
    override val profileRepository: ProfileRepository by lazy { FakeProfileRepository() }
}

class RemoteAppContainer(override val applicationContext: Context) : AppContainer {
    override val networkCacheRepository: NetworkCacheRepository by lazy {
        LocalNetworkCacheRepository(
            applicationContext
        )
    }
    override val scheduleRepository: ScheduleRepository by lazy {
        RemoteScheduleRepository(
            applicationContext
        )
    }
    override val profileRepository: ProfileRepository by lazy {
        RemoteProfileRepository(
            applicationContext
        )
    }
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