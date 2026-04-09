package com.example.dailytasks.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long,
    @SerialName("date_start") val dateStart: Long,
    @SerialName("date_finish") val dateFinish: Long,
    val name: String,
    val description: String
)
