package com.example.dailytasks.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dailytasks.presentation.create.CreateTaskScreen
import com.example.dailytasks.presentation.create.CreateTaskViewModel
import com.example.dailytasks.presentation.details.TaskDetailsScreen
import com.example.dailytasks.presentation.details.TaskDetailsViewModel
import com.example.dailytasks.presentation.edit.EditTaskScreen
import com.example.dailytasks.presentation.edit.EditTaskViewModel
import com.example.dailytasks.presentation.navigation.Destination
import com.example.dailytasks.presentation.schedule.ScheduleRoute
import com.example.dailytasks.presentation.schedule.ScheduleViewModel

@Composable
fun DailyTasksRoot() {
    val context = LocalContext.current.applicationContext
    val app = context as DailyTasksApp
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        app.appContainer.repository.initialize()
    }

    NavHost(
        navController = navController,
        startDestination = Destination.Schedule.route
    ) {
        composable(Destination.Schedule.route) {
            val viewModel: ScheduleViewModel = viewModel(
                factory = ScheduleViewModel.factory(app.appContainer)
            )

            ScheduleRoute(
                viewModel = viewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetails.createRoute(taskId))
                },
                onCreateTaskClick = {
                    navController.navigate(Destination.CreateTask.route)
                }
            )
        }

        composable(Destination.CreateTask.route) {
            val viewModel: CreateTaskViewModel = viewModel(
                factory = CreateTaskViewModel.factory(app.appContainer)
            )

            CreateTaskScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Destination.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable

            val viewModel: EditTaskViewModel = viewModel(
                factory = EditTaskViewModel.factory(app.appContainer, taskId)
            )

            EditTaskScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(Destination.Schedule.route, false)
                }
            )
        }

        composable(
            route = Destination.TaskDetails.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable

            val viewModel: TaskDetailsViewModel = viewModel(
                factory = TaskDetailsViewModel.factory(app.appContainer, taskId)
            )

            TaskDetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = {
                    navController.navigate(Destination.EditTask.createRoute(taskId))
                }
            )
        }
    }
}