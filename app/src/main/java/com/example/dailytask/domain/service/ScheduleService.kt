package com.example.dailytasks.domain.service

import com.example.dailytasks.core.formatTime
import com.example.dailytasks.core.toLocalDateTime
import com.example.dailytasks.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class TaskBlock(
    val taskId: Long,
    val title: String,
    val timeRange: String
)

data class HourSlot(
    val hour: Int,
    val label: String,
    val tasks: List<TaskBlock>
)

interface ScheduleService {
    fun buildSchedule(tasks: List<Task>, selectedDate: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): List<HourSlot>
}

class DefaultScheduleService : ScheduleService {

    override fun buildSchedule(tasks: List<Task>, selectedDate: LocalDate, zoneId: ZoneId): List<HourSlot> {
        return (0..23).map { hour ->
            val slotStart = selectedDate.atTime(hour, 0)
            val slotEnd = slotStart.plusHours(1)
            val slotTasks = tasks.filter { task ->
                val taskStart = task.startTimeMillis.toLocalDateTime(zoneId)
                val taskEnd = task.endTimeMillis.toLocalDateTime(zoneId)
                taskStart < slotEnd && taskEnd > slotStart
            }.sortedBy { it.startTimeMillis }
                .map { task ->
                    val start = task.startTimeMillis.toLocalDateTime(zoneId).toLocalTime()
                    val end = task.endTimeMillis.toLocalDateTime(zoneId).toLocalTime()
                    TaskBlock(
                        taskId = task.id,
                        title = task.name,
                        timeRange = "${formatTime(start)} - ${formatTime(end)}"
                    )
                }

            HourSlot(
                hour = hour,
                label = buildLabel(hour),
                tasks = slotTasks
            )
        }
    }

    private fun buildLabel(hour: Int): String {
        val start = LocalTime.of(hour, 0)
        val end = start.plusHours(1)
        return "${formatTime(start)} - ${formatTime(end)}"
    }
}
