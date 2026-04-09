package com.example.dailytasks.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.usecase.CreateTaskUseCase
import com.example.dailytasks.main.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class CreateTaskUiState(
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.of(9, 30),
    val endTime: LocalTime = LocalTime.of(10, 30),
    val description: String = "",
    val titleError: String? = null,
    val timeError: String? = null,
    val isSaved: Boolean = false
)

class CreateTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

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
            createTaskUseCase(
                NewTaskDraft(
                    name = current.title,
                    date = current.date,
                    startTime = current.startTime,
                    endTime = current.endTime,
                    description = current.description
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateTaskViewModel(CreateTaskUseCase(appContainer.repository)) as T
            }
        }
    }
}
