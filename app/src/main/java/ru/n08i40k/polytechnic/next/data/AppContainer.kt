package ru.n08i40k.polytechnic.next.data

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.data.schedule.impl.RemoteScheduleRepository
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.data.users.impl.RemoteProfileRepository
import javax.inject.Singleton

interface AppContainer {
    val scheduleRepository: ScheduleRepository
    val profileRepository: ProfileRepository
}

class MockAppContainer : AppContainer {
    override val scheduleRepository: ScheduleRepository by lazy { FakeScheduleRepository() }
    override val profileRepository: ProfileRepository by lazy { FakeProfileRepository() }
}

class RemoteAppContainer(private val applicationContext: Context) : AppContainer {
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