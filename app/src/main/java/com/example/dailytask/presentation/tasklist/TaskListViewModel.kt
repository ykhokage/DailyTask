package com.example.dailytasks.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository
import com.example.dailytasks.main.AppContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class TaskListUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val tasks: List<Task> = emptyList()
)

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TaskListUiState> = combine(
        selectedDate,
        selectedDate.flatMapLatest { date -> repository.observeTasksForDay(date) }
    ) { date, tasks ->
        TaskListUiState(selectedDate = date, tasks = tasks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState()
    )

    fun onDateSelected(date: LocalDate) {
        selectedDate.value = date
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskListViewModel(
                    repository = appContainer.repository
                ) as T
            }
        }
    }
}
