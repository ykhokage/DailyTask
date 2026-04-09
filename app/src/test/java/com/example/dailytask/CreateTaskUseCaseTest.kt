package com.example.dailytasks

import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository
import com.example.dailytasks.domain.usecase.CreateTaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CreateTaskUseCaseTest {

    @Test(expected = IllegalArgumentException::class)
    fun `create task throws when end time is before start time`() = runBlocking {
        val useCase = CreateTaskUseCase(FakeTaskRepository())
        useCase(
            NewTaskDraft(
                name = "Invalid",
                date = LocalDate.now(),
                startTime = LocalTime.of(12, 0),
                endTime = LocalTime.of(11, 0),
                description = ""
            )
        )
    }
}

private class FakeTaskRepository : TaskRepository {
    override suspend fun initialize() = Unit

    override fun observeTasksForDay(date: LocalDate): Flow<List<Task>> = emptyFlow()

    override suspend fun getTaskById(id: Long): Task? = null

    override suspend fun createTask(draft: NewTaskDraft) = Unit

    override suspend fun updateTask(task: Task) = Unit

    override suspend fun deleteTaskById(id: Long) = Unit
}