package com.example.dailytasks.domain.usecase

import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository

class UpdateTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
    }
}