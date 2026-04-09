package com.example.dailytasks.data.repository

import com.example.dailytasks.core.atEndOfDayMillis
import com.example.dailytasks.core.atStartOfDayMillis
import com.example.dailytasks.data.local.db.TaskDao
import com.example.dailytasks.data.local.source.AssetTaskDataSource
import com.example.dailytasks.data.mapper.toDomain
import com.example.dailytasks.data.mapper.toEntity
import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DefaultTaskRepository(
    private val taskDao: TaskDao,
    private val assetTaskDataSource: AssetTaskDataSource
) : TaskRepository {

    override suspend fun initialize() = withContext(Dispatchers.IO) {
        if (taskDao.count() == 0) {
            val initialTasks = assetTaskDataSource.loadTasks().map { it.toEntity() }
            taskDao.insertAll(initialTasks)
        }
    }

    override fun observeTasksForDay(date: LocalDate): Flow<List<Task>> {
        return taskDao.observeTasksForDay(
            dayStart = date.atStartOfDayMillis(),
            dayEnd = date.atEndOfDayMillis()
        ).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTaskById(id: Long): Task? = withContext(Dispatchers.IO) {
        taskDao.getById(id)?.toDomain()
    }

    override suspend fun createTask(draft: NewTaskDraft) = withContext(Dispatchers.IO) {
        val nextId = (taskDao.getMaxId() ?: 0L) + 1L
        taskDao.insert(draft.toEntity(nextId))
    }
}