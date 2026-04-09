package com.example.dailytasks.presentation.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color as AndroidColor
import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailytasks.core.formatDate
import com.example.dailytasks.core.formatTime
import com.example.dailytasks.ui.theme.AccentYellow
import com.example.dailytasks.ui.theme.AppBackground
import com.example.dailytasks.ui.theme.ChipInactive
import com.example.dailytasks.ui.theme.TextDark
import com.example.dailytasks.ui.theme.TextPrimary
import com.example.dailytasks.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.date
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
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onDateChanged(selectedDate)
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

    if (showStartTimePicker) {
        TimeSelectDialog(
            title = "Выбери время начала",
            initialTime = state.startTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                viewModel.onStartTimeChanged(it)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimeSelectDialog(
            title = "Выбери время окончания",
            initialTime = state.endTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                viewModel.onEndTimeChanged(it)
                showEndTimePicker = false
            }
        )
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Создание задачи",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard()

            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChanged,
                label = {
                    Text(
                        text = "Название",
                        color = TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = state.titleError != null,
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = createTaskTextFieldColors(),
                supportingText = {
                    state.titleError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            DateTimeActionCard(
                icon = Icons.Default.CalendarMonth,
                title = "Дата",
                value = formatDate(state.date),
                onClick = { showDatePicker = true }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateTimeActionCard(
                    icon = Icons.Default.AccessTime,
                    title = "Начало",
                    value = formatTime(state.startTime),
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.weight(1f)
                )

                DateTimeActionCard(
                    icon = Icons.Default.AccessTime,
                    title = "Окончание",
                    value = formatTime(state.endTime),
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            state.timeError?.let {
                ErrorCard(message = it)
            }

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = {
                    Text(
                        text = "Описание",
                        color = TextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(22.dp),
                colors = createTaskTextFieldColors()
            )

            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = TextDark
                )
            ) {
                Text(
                    text = "Сохранить",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF171717),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(ChipInactive, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                tint = AccentYellow
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Новая задача",
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Заполни название, выбери дату и время, затем сохрани событие.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DateTimeActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF171717),
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF2B2B2B),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(ChipInactive, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentYellow
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = title,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeSelectDialog(
    title: String,
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val minuteOptions = remember { (0..59).toList() }

    var selectedHour by remember { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember {
        mutableIntStateOf((initialTime.minute / 5) * 5)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF171717),
        title = {
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "%02d:%02d".format(selectedHour, selectedMinute),
                    color = AccentYellow,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    label = "Часы",
                    items = (0..23).toList(),
                    initialItem = initialTime.hour,
                    format = { "%02d".format(it) },
                    onItemSelected = { selectedHour = it }
                )

                WheelPicker(
                    modifier = Modifier.weight(1f),
                    label = "Минуты",
                    items = minuteOptions,
                    initialItem = minuteOptions.minBy { kotlin.math.abs(it - initialTime.minute) },
                    format = { "%02d".format(it) },
                    onItemSelected = { selectedMinute = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(LocalTime.of(selectedHour, selectedMinute))
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    modifier: Modifier = Modifier,
    label: String,
    items: List<Int>,
    initialItem: Int,
    format: (Int) -> String,
    onItemSelected: (Int) -> Unit
) {
    val visibleItemsCount = 5
    val centerIndex = visibleItemsCount / 2
    val itemHeight = 44.dp

    val startIndex = remember(items, initialItem) {
        val index = items.indexOf(initialItem).coerceAtLeast(0)
        index
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex
    )

    LaunchedEffect(listState, items) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> items.getOrElse(index) { items.first() } }
            .distinctUntilChanged()
            .collect { onItemSelected(it) }
    }

    Column(
        modifier = modifier
            .background(
                color = Color(0xFF111111),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF2B2B2B),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.height(itemHeight * visibleItemsCount)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(
                        Color(0xFF2A2730),
                        RoundedCornerShape(14.dp)
                    )
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(visibleItemsCount / 2) {
                    Spacer(modifier = Modifier.height(itemHeight))
                }

                itemsIndexed(items) { index, item ->
                    val isSelected = index == listState.firstVisibleItemIndex

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = format(item),
                            color = if (isSelected) TextPrimary else TextSecondary.copy(alpha = 0.65f),
                            style = if (isSelected) {
                                MaterialTheme.typography.headlineSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                items(visibleItemsCount / 2) {
                    Spacer(modifier = Modifier.height(itemHeight))
                }
            }
        }
    }
}

@Composable
private fun WheelPickerCard(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = Color(0xFF111111),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF2B2B2B),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
private fun createTaskTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = AccentYellow,
    unfocusedBorderColor = Color(0xFF5A5A5A),
    focusedLabelColor = AccentYellow,
    unfocusedLabelColor = TextSecondary,
    cursorColor = AccentYellow,
    focusedContainerColor = Color(0xFF0F0F10),
    unfocusedContainerColor = Color(0xFF0F0F10),
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error
)
