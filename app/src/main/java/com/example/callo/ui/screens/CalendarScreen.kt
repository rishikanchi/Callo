package com.example.callo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.model.Habit
import com.example.callo.viewmodel.MainViewModel
import com.example.compose.CalloTheme
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember {
        mutableStateOf(Month.entries.toTypedArray()[today.month.value - 1] to today.year)
    }

    var viewModel: MainViewModel = viewModel()
    var habits = viewModel.getAllHabits().toMutableList()
    var selectedHabit by remember { mutableStateOf<Habit?>(habits.firstOrNull()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Habit Tracking Calendar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                val (month, year) = currentMonth
                currentMonth = if (month == Month.JANUARY) {
                    Month.DECEMBER to (year - 1)
                } else {
                    Month.values()[month.ordinal - 1] to year
                }
            },
            onNextMonth = {
                val (month, year) = currentMonth
                currentMonth = if (month == Month.DECEMBER) {
                    Month.JANUARY to (year + 1)
                } else {
                    Month.values()[month.ordinal + 1] to year
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DayOfWeekHeader()

        CalendarGrid(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            selectedHabit = selectedHabit,
            habits = habits,
            onDateSelected = { selectedDate = it },
            onHabitSelected = { habit -> selectedHabit = habit }
        )

        Spacer(modifier = Modifier.height(24.dp))

        HabitsList(
            habits = habits,
            selectedHabit = selectedHabit,
            selectedDate = selectedDate,
            onHabitComplete = { habitId ->
                viewModel.updateHabit(habitId)
            },
            onHabitSelected = { habit -> selectedHabit = habit }
        )
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: Pair<Month, Int>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous month"
            )
        }

        Text(
            text = "${currentMonth.first.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${currentMonth.second}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next month"
            )
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        daysOfWeek.forEach { day ->
            Text(
                modifier = Modifier.weight(1f),
                text = day,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Pair<Month, Int>,
    selectedDate: LocalDate,
    selectedHabit: Habit?,
    habits: List<Habit>,
    onDateSelected: (LocalDate) -> Unit,
    onHabitSelected: (Habit) -> Unit
) {
    val (month, year) = currentMonth
    val firstOfMonth = LocalDate(year, month.value, 1)
    val startOffset = firstOfMonth.dayOfWeek.ordinal
    val daysInMonth = month.length(year.isLeapYear())

    val dates = buildList {
        repeat(startOffset) { add(null) }
        for (dayOfMonth in 1..daysInMonth) {
            add(LocalDate(year, month.value, dayOfMonth))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(dates) { date ->
            if (date == null) {
                Box(modifier = Modifier.aspectRatio(1f))
            } else {
                CalendarDay(
                    date = date,
                    isSelected = date == selectedDate,
                    isCompleted = selectedHabit?.isCompletedOn(date) ?: false,
                    onDateClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    isCompleted: Boolean,
    onDateClick: () -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onDateClick),
        shape = MaterialTheme.shapes.small,
        color = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            isCompleted -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        border = if (isToday) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    isCompleted -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun HabitsList(
    habits: List<Habit>,
    selectedHabit: Habit?,
    selectedDate: LocalDate,
    onHabitComplete: (Int) -> Unit,
    onHabitSelected: (Habit) -> Unit
) {
    Column {
        Text(
            text = "Check your habits:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    isSelected = habit == selectedHabit,
                    selectedDate = selectedDate,
                    onComplete = { onHabitComplete(habit.id) },
                    onSelect = { onHabitSelected(habit) }
                )
            }
        }
    }
}

@Composable
private fun HabitItem(
    habit: Habit,
    isSelected: Boolean,
    selectedDate: LocalDate,
    onComplete: () -> Unit,
    onSelect: () -> Unit
) {
    var isChecked by remember { mutableStateOf(habit.isCompletedOn(selectedDate)) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { newCheckedState ->
                    isChecked = newCheckedState
                    onComplete()
                                  },
                modifier = Modifier.clickable(onClick = onComplete),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

// Helper function to determine leap years
private fun Int.isLeapYear(): Boolean {
    return this % 4 == 0 && (this % 100 != 0 || this % 400 == 0)
}



