package com.example.callo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Preview()
@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Statistics",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GeneralStatsCard() }
            item { ProductivityIndexCard() }
            item { HabitAnalyticsSection() }
        }
    }
}

@Composable
private fun GeneralStatsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "General Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatItem("Tasks Completed", "42")
            StatItem("Current Streak", "7 days")
            StatItem("Average Daily Tasks", "5.3")
        }
    }
}

@Composable
private fun ProductivityIndexCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Productivity Index",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "75% productive this week",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HabitAnalyticsSection() {
    var expandedHabitId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Habit Analytics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        sampleHabitAnalytics.forEach { habitAnalytic ->
            HabitAnalyticCard(
                habitAnalytic = habitAnalytic,
                isExpanded = expandedHabitId == habitAnalytic.id,
                onExpandClick = {
                    expandedHabitId = if (expandedHabitId == habitAnalytic.id) null else habitAnalytic.id
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HabitAnalyticCard(
    habitAnalytic: HabitAnalytic,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = habitAnalytic.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${habitAnalytic.completionRate}% completion rate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onExpandClick) {
                    Icon(
                        imageVector = if (isExpanded)
                            Icons.Default.KeyboardArrowDown
                        else
                            Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                StatItem("Longest Streak", "${habitAnalytic.longestStreak} days")
                StatItem("Current Streak", "${habitAnalytic.currentStreak} days")
                StatItem("Total Completions", habitAnalytic.totalCompletions.toString())
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

data class HabitAnalytic(
    val id: String,
    val name: String,
    val completionRate: Int,
    val longestStreak: Int,
    val currentStreak: Int,
    val totalCompletions: Int
)

private val sampleHabitAnalytics = listOf(
    HabitAnalytic(
        id = "1",
        name = "Morning Meditation",
        completionRate = 85,
        longestStreak = 14,
        currentStreak = 5,
        totalCompletions = 42
    ),
    HabitAnalytic(
        id = "2",
        name = "Daily Exercise",
        completionRate = 70,
        longestStreak = 10,
        currentStreak = 3,
        totalCompletions = 35
    ),
    HabitAnalytic(
        id = "3",
        name = "Read 30 Minutes",
        completionRate = 90,
        longestStreak = 21,
        currentStreak = 21,
        totalCompletions = 60
    )
)