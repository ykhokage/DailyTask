package com.example.dailytasks.presentation.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.ui.theme.AccentYellow
import com.example.dailytasks.ui.theme.AppBackground
import com.example.dailytasks.ui.theme.CardGreen
import com.example.dailytasks.ui.theme.CardLight
import com.example.dailytasks.ui.theme.CardPink
import com.example.dailytasks.ui.theme.CardYellow
import com.example.dailytasks.ui.theme.ChipInactive
import com.example.dailytasks.ui.theme.IconDark
import com.example.dailytasks.ui.theme.TextDark
import com.example.dailytasks.ui.theme.TextPrimary
import com.example.dailytasks.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TaskListRoute(
    viewModel: TaskListViewModel,
    onTaskClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onOpenScheduleClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TaskListScreen(
        selectedDate = state.selectedDate,
        tasks = state.tasks,
        onDateSelected = viewModel::onDateSelected,
        onTaskClick = onTaskClick,
        onCreateClick = onCreateClick,
        onOpenScheduleClick = onOpenScheduleClick
    )
}

@Composable
fun TaskListScreen(
    selectedDate: LocalDate,
    tasks: List<Task>,
    onDateSelected: (LocalDate) -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onOpenScheduleClick: () -> Unit
) {
    val visibleDates = buildDateRange(selectedDate)

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            BottomCapsuleBar(
                onHomeClick = {},
                onCalendarClick = onOpenScheduleClick,
                onSettingsClick = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HeaderSection(onCreateClick = onCreateClick)
            }

            item {
                Column {
                    Text(
                        text = "Мои задачи",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Выбери день и управляй делами",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            item {
                DateSelectorRow(
                    dates = visibleDates,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
                )
            }

            if (tasks.isEmpty()) {
                item {
                    EmptyTasksCard(onCreateClick = onCreateClick)
                }
            } else {
                items(tasks.sortedBy { it.startTimeMillis }) { task ->
                    StylishTaskCard(
                        task = task,
                        colorIndex = task.id.toInt(),
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CreateTaskButton(onClick = onCreateClick)

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ChipInactive),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DT",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CreateTaskButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AccentYellow)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(IconDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Создать задачу",
            color = TextDark,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DateSelectorRow(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(dates) { date ->
            val isSelected = date == selectedDate

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) AccentYellow else ChipInactive)
                    .clickable { onDateSelected(date) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))),
                    color = if (isSelected) TextDark else TextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (date == LocalDate.now()) "Сегодня"
                    else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                    color = if (isSelected) TextDark else TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StylishTaskCard(
    task: Task,
    colorIndex: Int,
    onClick: () -> Unit
) {
    val cardColor = when (colorIndex % 4) {
        0 -> CardGreen
        1 -> CardYellow
        2 -> CardLight
        else -> CardPink
    }

    val start = millisToLocalDate(task.startTimeMillis)
    val dateText = start.format(DateTimeFormatter.ofPattern("dd MMM", Locale("ru")))
    val timeText = buildTimeRangeText(task.startTimeMillis, task.endTimeMillis)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(IconDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = AccentYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = dateText,
                    color = TextDark,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeText,
                    color = TextDark,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = task.description.ifBlank { "Без описания" },
                color = TextDark.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(TextPrimary.copy(alpha = 0.95f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DailyTasks",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor.copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Запланировано",
                        color = TextDark,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTasksCard(
    onCreateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardLight)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "На этот день задач нет",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Создай новую задачу, чтобы она появилась в расписании выбранного дня.",
                color = TextDark.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentYellow)
                    .clickable(onClick = onCreateClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Добавить задачу",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BottomCapsuleBar(
    onHomeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(ChipInactive)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomIconItem(
            selected = true,
            icon = Icons.Default.Home,
            onClick = onHomeClick
        )

        BottomIconItem(
            selected = false,
            icon = Icons.Default.CalendarMonth,
            onClick = onCalendarClick
        )

        BottomIconItem(
            selected = false,
            icon = Icons.Default.Schedule,
            onClick = onCalendarClick
        )

        BottomIconItem(
            selected = false,
            icon = Icons.Default.Settings,
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun BottomIconItem(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (selected) AccentYellow else AppBackground.copy(alpha = 0.35f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) TextDark else TextPrimary
        )
    }
}

private fun buildDateRange(selectedDate: LocalDate): List<LocalDate> {
    return (-3..3).map { selectedDate.plusDays(it.toLong()) }
}

private fun millisToLocalDate(millis: Long): LocalDate {
    return java.time.Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun buildTimeRangeText(startMillis: Long, endMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val start = java.time.Instant.ofEpochMilli(startMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    val end = java.time.Instant.ofEpochMilli(endMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return "${start.format(formatter)} - ${end.format(formatter)}"
}