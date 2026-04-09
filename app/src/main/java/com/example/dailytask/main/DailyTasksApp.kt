package com.example.dailytasks.main

import android.app.Application
import com.example.dailytasks.data.local.db.AppDatabase
import com.example.dailytasks.data.local.source.AssetTaskDataSource
import com.example.dailytasks.data.repository.DefaultTaskRepository
import com.example.dailytasks.domain.repository.TaskRepository
import com.example.dailytasks.domain.service.DefaultScheduleService
import com.example.dailytasks.domain.service.ScheduleService

class DailyTasksApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}

interface AppContainer {
    val repository: TaskRepository
    val scheduleService: ScheduleService
}

class DefaultAppContainer(application: Application) : AppContainer {
    private val database = AppDatabase.create(application)
    private val assetTaskDataSource = AssetTaskDataSource(application.assets)

    override val repository: TaskRepository by lazy {
        DefaultTaskRepository(
            taskDao = database.taskDao(),
            assetTaskDataSource = assetTaskDataSource
        )
    }

    override val scheduleService: ScheduleService = DefaultScheduleService()
}
