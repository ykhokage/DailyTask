package com.example.dailytasks.presentation.schedule

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytasks.ui.theme.AccentYellow
import com.example.dailytasks.ui.theme.AppBackground
import com.example.dailytasks.ui.theme.CardGreen
import com.example.dailytasks.ui.theme.CardLight
import com.example.dailytasks.ui.theme.CardPink
import com.example.dailytasks.ui.theme.CardYellow
import com.example.dailytasks.ui.theme.ChipInactive
import com.example.dailytasks.ui.theme.TextDark
import com.example.dailytasks.ui.theme.TextPrimary
import com.example.dailytasks.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRoute(
    viewModel: ScheduleViewModel,
    onTaskClick: (Long) -> Unit,
    onCreateTaskClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ScheduleScreen(
        selectedDate = state.selectedDate,
        hourSlots = state.hourSlots,
        onDateSelected = viewModel::onDateSelected,
        onTaskClick = onTaskClick,
        onCreateClick = onCreateTaskClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    selectedDate: LocalDate,
    hourSlots: List<HourSlotUi>,
    onDateSelected: (LocalDate) -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateClick: () -> Unit
) {
    val visibleDates = remember(selectedDate) {
        (-3..3).map { selectedDate.plusDays(it.toLong()) }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val newDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(newDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = AppBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = AccentYellow,
                contentColor = TextDark,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 10.dp,
                end = 10.dp,
                top = 12.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScheduleHeader(
                    onCalendarClick = { showDatePicker = true }
                )
            }

            item {
                DateSelectorRow(
                    dates = visibleDates,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
                )
            }

            items(hourSlots) { slot ->
                TimelineSlotCard(
                    slot = slot,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}

@Composable
private fun ScheduleHeader(
    onCalendarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF171717))
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ChipInactive)
                    .clickable(onClick = onCalendarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Открыть календарь",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Мои задачи",
            color = TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Выбери день и управляй расписанием",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DateSelectorRow(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(dates) { date ->
            val selected = date == selectedDate

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (selected) AccentYellow else ChipInactive)
                    .clickable { onDateSelected(date) }
                    .padding(horizontal = 15.dp, vertical = 11.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))),
                    color = if (selected) TextDark else TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (date == LocalDate.now()) "Сегодня"
                    else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                    color = if (selected) TextDark else TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TimelineSlotCard(
    slot: HourSlotUi,
    onTaskClick: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(58.dp)
        ) {
            Text(
                text = slot.timeRangeText.replace(" - ", "\n"),
                color = TextPrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(if (slot.tasks.isEmpty()) 46.dp else (slot.tasks.size * 96).dp)
                    .background(Color(0xFF2C2C2C))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (slot.tasks.isEmpty()) {
                EmptySlotCard()
            } else {
                slot.tasks.forEachIndexed { index, task ->
                    TaskTimelineCard(
                        task = task,
                        color = cardColorByIndex(task.taskId.toInt() + index),
                        onClick = { onTaskClick(task.taskId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySlotCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A2730)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7B7486))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Свободно",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TaskTimelineCard(
    task: TaskBlockUi,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        color = TextDark,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Spacer(modifier = Modifier.width(14.dp))

                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = task.timeRange,
                            color = TextDark,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.86f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AccentYellow
                    )
                }
            }
        }
    }
}

private fun cardColorByIndex(index: Int): Color {
    return when (index % 4) {
        0 -> CardGreen
        1 -> CardYellow
        2 -> CardLight
        else -> CardPink
    }
}