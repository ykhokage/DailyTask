package com.example.dailytasks.presentation.navigation

sealed class Destination(val route: String) {
    data object Schedule : Destination("schedule")
    data object CreateTask : Destination("create")
    data object EditTask : Destination("edit/{taskId}") {
        fun createRoute(taskId: Long): String = "edit/$taskId"
    }
    data object TaskDetails : Destination("details/{taskId}") {
        fun createRoute(taskId: Long): String = "details/$taskId"
    }
}