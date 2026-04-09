package com.example.dailytasks.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.usecase.GetTaskDetailsUseCase
import com.example.dailytasks.domain.usecase.UpdateTaskUseCase
import com.example.dailytasks.main.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class EditTaskUiState(
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.of(9, 30),
    val endTime: LocalTime = LocalTime.of(10, 30),
    val description: String = "",
    val titleError: String? = null,
    val timeError: String? = null,
    val isSaved: Boolean = false
)

class EditTaskViewModel(
    private val taskId: Long,
    private val getTaskDetailsUseCase: GetTaskDetailsUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = getTaskDetailsUseCase(taskId)
            _uiState.value = if (task == null) {
                EditTaskUiState(
                    isLoading = false,
                    notFound = true
                )
            } else {
                val zoneId = ZoneId.systemDefault()
                val startDateTime = Instant.ofEpochMilli(task.startTimeMillis).atZone(zoneId).toLocalDateTime()
                val endDateTime = Instant.ofEpochMilli(task.endTimeMillis).atZone(zoneId).toLocalDateTime()

                EditTaskUiState(
                    isLoading = false,
                    notFound = false,
                    title = task.name,
                    date = startDateTime.toLocalDate(),
                    startTime = startDateTime.toLocalTime(),
                    endTime = endDateTime.toLocalTime(),
                    description = task.description
                )
            }
        }
    }

    fun onTitleChanged(value: String) {
        _uiState.value = _uiState.value.copy(title = value, titleError = null)
    }

    fun onDateChanged(value: LocalDate) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun onStartTimeChanged(value: LocalTime) {
        _uiState.value = _uiState.value.copy(startTime = value, timeError = null)
    }

    fun onEndTimeChanged(value: LocalTime) {
        _uiState.value = _uiState.value.copy(endTime = value, timeError = null)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun save() {
        val current = _uiState.value

        if (current.title.isBlank()) {
            _uiState.value = current.copy(titleError = "Введите название задачи")
            return
        }

        if (current.endTime <= current.startTime) {
            _uiState.value = current.copy(timeError = "Время окончания должно быть позже времени начала")
            return
        }

        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val startMillis = current.date.atTime(current.startTime).atZone(zoneId).toInstant().toEpochMilli()
            val endMillis = current.date.atTime(current.endTime).atZone(zoneId).toInstant().toEpochMilli()

            updateTaskUseCase(
                Task(
                    id = taskId,
                    startTimeMillis = startMillis,
                    endTimeMillis = endMillis,
                    name = current.title,
                    description = current.description
                )
            )

            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    companion object {
        fun factory(appContainer: AppContainer, taskId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EditTaskViewModel(
                        taskId = taskId,
                        getTaskDetailsUseCase = GetTaskDetailsUseCase(appContainer.repository),
                        updateTaskUseCase = UpdateTaskUseCase(appContainer.repository)
                    ) as T
                }
            }
    }
}