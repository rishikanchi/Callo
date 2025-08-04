package com.example.callo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.model.Event
import com.example.callo.viewmodel.MainViewModel
import com.example.compose.CalloTheme
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    var viewModel: MainViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Start",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Date") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("YYYY-MM-DD") }
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Time") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("HH:MM") }
                )
            }

            Text(
                text = "End",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Date") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("YYYY-MM-DD") }
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Time") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("HH:MM") }
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            Button(
                onClick = {
                    val startDateTime = LocalDateTime.parse("${startDate}T${startTime}:00")
                    val endDateTime = LocalDateTime.parse("${endDate}T${endTime}:00")

                    viewModel.createEvent(Event(
                              id = (100000000..999999999).random(),
                              title = title,
                              userId = viewModel.currentUser.value!!.id,
                              description = description,
                              startTime = startDateTime,
                              endTime = endDateTime,
                          )
                    )

                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Event")
            }
        }
    }
}

@Preview(
    name = "Light Theme",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CreateEventScreenPreview() {
    CalloTheme {
        CreateEventScreen()
    }
}