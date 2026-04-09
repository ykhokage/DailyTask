package com.example.dailytasks.data.local.source

import android.content.res.AssetManager
import com.example.dailytasks.data.local.model.TaskDto
import kotlinx.serialization.json.Json

class AssetTaskDataSource(
    private val assetManager: AssetManager,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun loadTasks(): List<TaskDto> {
        val rawJson = assetManager.open(FILE_NAME).bufferedReader().use { it.readText() }
        return json.decodeFromString(rawJson)
    }

    private companion object {
        const val FILE_NAME = "tasks.json"
    }
}
