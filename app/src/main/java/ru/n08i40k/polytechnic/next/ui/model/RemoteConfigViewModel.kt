package ru.n08i40k.polytechnic.next.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.n08i40k.polytechnic.next.ui.MainActivity
import java.util.logging.Logger

data class RemoteConfigUiState(
    val minVersion: String,
    val currVersion: String,
    val serverVersion: String,
    val downloadLink: String,
    val telegramLink: String,
    val linkUpdateDelay: Long,
)


class RemoteConfigViewModel(
    private val appContext: Context,
    private val remoteConfig: FirebaseRemoteConfig,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        RemoteConfigUiState(
            minVersion = remoteConfig.getString("minVersion"),
            currVersion = remoteConfig.getString("currVersion"),
            downloadLink = remoteConfig.getString("downloadLink"),
            telegramLink = remoteConfig.getString("telegramLink"),
            serverVersion = remoteConfig.getString("serverVersion"),
            linkUpdateDelay = remoteConfig.getLong("linkUpdateDelay"),
        )
    )

    val uiState = viewModelState
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value)

    init {
        (appContext as MainActivity)
            .scheduleLinkUpdate(viewModelState.value.linkUpdateDelay)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    viewModelState.update {
                        it.copy(
                            minVersion = remoteConfig.getString("minVersion"),
                            currVersion = remoteConfig.getString("currVersion"),
                            downloadLink = remoteConfig.getString("downloadLink"),
                            telegramLink = remoteConfig.getString("telegramLink"),
                            serverVersion = remoteConfig.getString("serverVersion"),
                            linkUpdateDelay = remoteConfig.getLong("linkUpdateDelay"),
                        )
                    }

                    appContext.scheduleLinkUpdate(viewModelState.value.linkUpdateDelay)
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Logger.getLogger("RemoteConfigViewModel")
                    .severe("Failed to fetch RemoteConfig update!")
            }
        })
    }

    companion object {
        fun provideFactory(
            appContext: Context,
            remoteConfig: FirebaseRemoteConfig,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST") return RemoteConfigViewModel(
                        appContext,
                        remoteConfig,
                    ) as T
                }
            }
    }
}