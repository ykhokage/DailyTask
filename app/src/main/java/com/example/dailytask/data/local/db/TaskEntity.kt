package com.example.dailytasks.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Long,
    val dateStart: Long,
    val dateFinish: Long,
    val name: String,
    val description: String
)
