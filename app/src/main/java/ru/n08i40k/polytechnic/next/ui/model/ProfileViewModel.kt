package ru.n08i40k.polytechnic.next.ui.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile

sealed interface ProfileUiState {
    val isLoading: Boolean

    data class NoProfile(
        override val isLoading: Boolean
    ) : ProfileUiState

    data class HasProfile(
        val profile: Profile,
        override val isLoading: Boolean
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val profile: Profile? = null,
    val isLoading: Boolean = false
) {
    fun toUiState(): ProfileUiState = if (profile == null) {
        ProfileUiState.NoProfile(isLoading)
    } else {
        ProfileUiState.HasProfile(profile, isLoading)
    }
}


class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    val onUnauthorized: () -> Unit
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map(ProfileViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        refreshProfile()
    }

    fun refreshProfile(callback: () -> Unit = {}) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = profileRepository.getProfile()

            viewModelState.update {
                when (result) {
                    is MyResult.Success -> it.copy(profile = result.data, isLoading = false)
                    is MyResult.Failure -> it.copy(profile = null, isLoading = false)
                }
            }

            callback()
        }
    }

    companion object {
        fun provideFactory(
            profileRepository: ProfileRepository,
            onUnauthorized: () -> Unit
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST") return ProfileViewModel(
                        profileRepository,
                        onUnauthorized
                    ) as T
                }
            }
    }
}

var Context.profileViewModel: ProfileViewModel? by mutableStateOf(null)