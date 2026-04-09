package com.example.dailytasks.domain.model

data class Task(
    val id: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val name: String,
    val description: String
)
