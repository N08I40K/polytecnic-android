package ru.n08i40k.polytechnic.next.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.UpdateDates
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.Group
import java.util.Date
import javax.inject.Inject

sealed interface ScheduleUiState {
    val isLoading: Boolean

    data class NoSchedule(
        override val isLoading: Boolean
    ) : ScheduleUiState

    data class HasSchedule(
        val group: Group,
        val updateDates: UpdateDates,
        val lastUpdateAt: Long,
        override val isLoading: Boolean
    ) : ScheduleUiState
}

private data class ScheduleViewModelState(
    val group: Group? = null,
    val updateDates: UpdateDates? = null,
    val lastUpdateAt: Long = 0,
    val isLoading: Boolean = false
) {
    fun toUiState(): ScheduleUiState = if (group == null) {
        ScheduleUiState.NoSchedule(isLoading)
    } else {
        ScheduleUiState.HasSchedule(group, updateDates!!, lastUpdateAt, isLoading)
    }
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleRepository = appContainer.scheduleRepository
    private val networkCacheRepository = appContainer.networkCacheRepository
    private val viewModelState = MutableStateFlow(ScheduleViewModelState(isLoading = true))

    val uiState = viewModelState
        .map(ScheduleViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        refreshGroup()
    }

    fun refreshGroup() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = scheduleRepository.getGroup()

            viewModelState.update {
                when (result) {
                    is MyResult.Success -> {
                        val updateDates = networkCacheRepository.getUpdateDates()

                        it.copy(
                            group = result.data,
                            updateDates = updateDates,
                            lastUpdateAt = Date().time,
                            isLoading = false
                        )
                    }

                    is MyResult.Failure -> it.copy(
                        group = null,
                        isLoading = false
                    )
                }
            }
        }
    }
}
