package com.example.dailytasks.domain.usecase

import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.repository.TaskRepository
import com.example.dailytasks.domain.service.HourSlot
import com.example.dailytasks.domain.service.ScheduleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ObserveDayScheduleUseCase(
    private val repository: TaskRepository,
    private val scheduleService: ScheduleService
) {
    operator fun invoke(date: LocalDate): Flow<List<HourSlot>> =
        repository.observeTasksForDay(date).map { tasks ->
            scheduleService.buildSchedule(tasks = tasks, selectedDate = date)
        }
}
