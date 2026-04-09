package com.example.dailytasks.domain.repository

import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    suspend fun initialize()
    fun observeTasksForDay(date: LocalDate): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun createTask(draft: NewTaskDraft)

    suspend fun updateTask(task: Task)
    suspend fun deleteTaskById(id: Long)
}