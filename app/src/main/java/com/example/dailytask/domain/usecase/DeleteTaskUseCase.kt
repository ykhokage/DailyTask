package com.example.dailytasks.domain.usecase

import com.example.dailytasks.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        repository.deleteTaskById(taskId)
    }
}