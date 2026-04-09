package com.example.dailytasks.domain.usecase

import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(draft: NewTaskDraft) {
        require(draft.name.isNotBlank()) { "Task title must not be empty" }
        require(draft.endTime > draft.startTime) { "End time must be after start time" }
        repository.createTask(draft)
    }
}
