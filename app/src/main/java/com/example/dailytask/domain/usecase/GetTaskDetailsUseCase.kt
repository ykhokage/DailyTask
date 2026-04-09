package com.example.dailytasks.domain.usecase

import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository

class GetTaskDetailsUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Task? = repository.getTaskById(taskId)
}
