package com.example.dailytasks.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.domain.service.HourSlot
import com.example.dailytasks.domain.usecase.ObserveDayScheduleUseCase
import com.example.dailytasks.main.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class ScheduleUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val hourSlots: List<HourSlotUi> = emptyList()
)

class ScheduleViewModel(
    private val observeDayScheduleUseCase: ObserveDayScheduleUseCase
) : ViewModel() {

    private val selectedDateFlow = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<ScheduleUiState> = combine(
        selectedDateFlow,
        selectedDateFlow.flatMapLatest { date ->
            observeDayScheduleUseCase(date)
        }
    ) { date, slots ->
        ScheduleUiState(
            selectedDate = date,
            hourSlots = slots.map { it.toUi() }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScheduleUiState()
    )

    fun onDateSelected(date: LocalDate) {
        selectedDateFlow.value = date
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ScheduleViewModel(
                        observeDayScheduleUseCase = ObserveDayScheduleUseCase(
                            repository = appContainer.repository,
                            scheduleService = appContainer.scheduleService
                        )
                    ) as T
                }
            }
    }
}

private fun HourSlot.toUi(): HourSlotUi {
    return HourSlotUi(
        timeRangeText = label,
        tasks = tasks.map { task ->
            TaskBlockUi(
                taskId = task.taskId,
                title = task.title,
                timeRange = task.timeRange
            )
        }
    )
}