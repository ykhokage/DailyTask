package com.example.dailytasks.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.core.formatDateTime
import com.example.dailytasks.core.toLocalDateTime
import com.example.dailytasks.domain.usecase.DeleteTaskUseCase
import com.example.dailytasks.domain.usecase.GetTaskDetailsUseCase
import com.example.dailytasks.main.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskDetailsUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val period: String = "",
    val description: String = "",
    val notFound: Boolean = false,
    val isDeleted: Boolean = false
)

class TaskDetailsViewModel(
    private val taskId: Long,
    private val getTaskDetailsUseCase: GetTaskDetailsUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState: StateFlow<TaskDetailsUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = getTaskDetailsUseCase(taskId)
            _uiState.value = if (task == null) {
                TaskDetailsUiState(isLoading = false, notFound = true)
            } else {
                TaskDetailsUiState(
                    isLoading = false,
                    title = task.name,
                    period = "${formatDateTime(task.startTimeMillis.toLocalDateTime())} — ${formatDateTime(task.endTimeMillis.toLocalDateTime())}",
                    description = task.description
                )
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }

    companion object {
        fun factory(appContainer: AppContainer, taskId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskDetailsViewModel(
                        taskId = taskId,
                        getTaskDetailsUseCase = GetTaskDetailsUseCase(appContainer.repository),
                        deleteTaskUseCase = DeleteTaskUseCase(appContainer.repository)
                    ) as T
                }
            }
    }
}