package com.example.dailytasks.data.mapper

import com.example.dailytasks.core.toEpochMillis
import com.example.dailytasks.data.local.db.TaskEntity
import com.example.dailytasks.data.local.model.TaskDto
import com.example.dailytasks.domain.model.NewTaskDraft
import com.example.dailytasks.domain.model.Task

fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = id,
    dateStart = dateStart,
    dateFinish = dateFinish,
    name = name,
    description = description
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    startTimeMillis = dateStart,
    endTimeMillis = dateFinish,
    name = name,
    description = description
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    dateStart = startTimeMillis,
    dateFinish = endTimeMillis,
    name = name.trim(),
    description = description.trim()
)

fun NewTaskDraft.toEntity(nextId: Long): TaskEntity = TaskEntity(
    id = nextId,
    dateStart = date.atTime(startTime).toEpochMillis(),
    dateFinish = date.atTime(endTime).toEpochMillis(),
    name = name.trim(),
    description = description.trim()
)