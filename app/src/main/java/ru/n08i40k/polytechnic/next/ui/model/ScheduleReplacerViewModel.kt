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
import ru.n08i40k.polytechnic.next.data.AppContainer
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer
import javax.inject.Inject

sealed interface ScheduleReplacerUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean,
    ) : ScheduleReplacerUiState

    data class HasData(
        override val isLoading: Boolean,
        val replacers: List<ScheduleReplacer>,
    ) : ScheduleReplacerUiState
}

private data class ScheduleReplacerViewModelState(
    val isLoading: Boolean = false,
    val replacers: List<ScheduleReplacer>? = null,
) {
    fun toUiState(): ScheduleReplacerUiState =
        if (replacers == null)
            ScheduleReplacerUiState.NoData(isLoading)
        else
            ScheduleReplacerUiState.HasData(isLoading, replacers)
}

@HiltViewModel
class ScheduleReplacerViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleReplacerRepository = appContainer.scheduleReplacerRepository
    private val viewModelState = MutableStateFlow(ScheduleReplacerViewModelState(isLoading = true))

    val uiState = viewModelState
        .map(ScheduleReplacerViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        refresh()
    }

    fun refresh() {
        setLoading()

        viewModelScope.launch { update() }
    }

    fun set(
        fileName: String,
        fileData: ByteArray,
        fileType: String
    ) {
        setLoading()

        viewModelScope.launch {
            val result = scheduleReplacerRepository.setCurrent(fileName, fileData, fileType)

            if (result is MyResult.Success) update()
            else setLoading(false)
        }
    }

    fun clear() {
        setLoading()

        viewModelScope.launch {
            val result = scheduleReplacerRepository.clear()

            viewModelState.update {
                when (result) {
                    is MyResult.Failure -> it.copy(isLoading = false)
                    is MyResult.Success -> it.copy(isLoading = false, replacers = emptyList())
                }
            }
        }
    }

    private fun setLoading(loading: Boolean = true) {
        viewModelState.update { it.copy(isLoading = loading) }
    }

    private suspend fun update() {
        val result = scheduleReplacerRepository.getAll()

        viewModelState.update {
            when (result) {
                is MyResult.Success -> {
                    it.copy(
                        replacers = result.data,
                        isLoading = false
                    )
                }

                is MyResult.Failure -> it.copy(
                    replacers = null,
                    isLoading = false
                )
            }
        }
    }
}
