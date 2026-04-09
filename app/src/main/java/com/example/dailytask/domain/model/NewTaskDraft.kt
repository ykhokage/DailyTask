package com.example.dailytasks.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class NewTaskDraft(
    val name: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val description: String
)
