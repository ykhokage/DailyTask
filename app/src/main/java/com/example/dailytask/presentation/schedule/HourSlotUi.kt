package com.example.dailytasks.presentation.schedule

data class TaskBlockUi(
    val taskId: Long,
    val title: String,
    val timeRange: String
)

data class HourSlotUi(
    val timeRangeText: String,
    val tasks: List<TaskBlockUi>
)