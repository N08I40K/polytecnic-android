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
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import java.util.Date
import javax.inject.Inject

sealed interface TeacherScheduleUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : TeacherScheduleUiState

    data class HasData(
        val teacher: GroupOrTeacher,
        val updateDates: UpdateDates,
        val lastUpdateAt: Long,
        override val isLoading: Boolean
    ) : TeacherScheduleUiState
}

private data class TeacherScheduleViewModelState(
    val teacher: GroupOrTeacher? = null,
    val updateDates: UpdateDates? = null,
    val lastUpdateAt: Long = 0,
    val isLoading: Boolean = false
) {
    fun toUiState(): TeacherScheduleUiState = if (teacher == null) {
        TeacherScheduleUiState.NoData(isLoading)
    } else {
        TeacherScheduleUiState.HasData(teacher, updateDates!!, lastUpdateAt, isLoading)
    }
}

@HiltViewModel
class TeacherScheduleViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleRepository = appContainer.scheduleRepository
    private val networkCacheRepository = appContainer.networkCacheRepository
    private val viewModelState = MutableStateFlow(TeacherScheduleViewModelState(isLoading = true))

    val uiState = viewModelState
        .map(TeacherScheduleViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        fetch(null)
    }

    fun fetch(name: String?) {
        if (name == null) {
            viewModelState.update {
                it.copy(
                    teacher = null,
                    isLoading = false
                )
            }
            return
        }

        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = scheduleRepository.getTeacher(name)

            viewModelState.update {
                when (result) {
                    is MyResult.Success -> {
                        val updateDates = networkCacheRepository.getUpdateDates()

                        it.copy(
                            teacher = result.data,
                            updateDates = updateDates,
                            lastUpdateAt = Date().time,
                            isLoading = false
                        )
                    }

                    is MyResult.Failure -> it.copy(
                        teacher = null,
                        isLoading = false
                    )
                }
            }
        }
    }
}
