package com.example.callo.ui.screens

import CerebrasService
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

@Composable
fun AIScreen(modifier: Modifier = Modifier) {
    var viewModel: MainViewModel = viewModel()
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val cerebrasService = remember {
        CerebrasService("csk-nwj9ppct8kjwc43rtrwnnee6yxve46wmrydhv4jpn5yf2hec")
    }

    var conversationId by remember { mutableStateOf<String?>(null) }

    val events = viewModel.getAllEvents()
    val tasks = viewModel.getAllTasks()
    val habits = viewModel.getAllHabits()
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    LaunchedEffect(Unit) {
        if (conversationId == null) {
            val systemContext = """
                You are an AI assistant for the Callo productivity app. Here is the current data:
                Events: ${events.joinToString("\n") { "- ${it.title} (${it.startTime} to ${it.endTime})" }}
                Tasks: ${tasks.joinToString("\n") { "- ${it.title} (due: ${it.dueDate})" }}
                Habits: ${habits.joinToString("\n") { "- ${it.title}" }}
                Current date/time: $currentDateTime
                User's Name: ${viewModel.currentUser.value!!.name}
                
                Your role is to help users manage their tasks, events, and habits. Be concise, helpful, and friendly.
                Try to be friendly and act like a fellow human friend, in terms of texting style and content (however, be brief).
                Most importantly, make sure you double-check that you are providing correct answers by doing your math correctly with respect to dates and times.
            """.trimIndent()

            conversationId = cerebrasService.createConversation(systemContext)

            if (messages.isEmpty()) {
                messages.add(ChatMessage(
                    "Hi! I'm your Callo AI assistant. I can help you manage your tasks, events, and habits. What would you like to know?",
                    false
                ))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI Assistant",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        ChatMessages(
            messages = messages,
            modifier = Modifier.weight(1f),
            listState = listState
        )

        Spacer(modifier = Modifier.height(8.dp))

        ChatInput(
            value = messageText,
            onValueChange = { messageText = it },
            onSendMessage = {
                if (messageText.isNotBlank() && conversationId != null) {
                    val userMessage = messageText
                    messages.add(ChatMessage(userMessage, true))

                    coroutineScope.launch {
                        try {
                            val response = cerebrasService.chat(
                                conversationId = conversationId!!,
                                userMessage = userMessage
                            )

                            messages.add(ChatMessage(response, false))
                        } catch (e: Exception) {
                            Log.e("AIScreen", "Error sending message", e)
                            val errorMessage = "Sorry, I'm having trouble connecting right now (${e.message}). Please try again later."
                            messages.add(ChatMessage(errorMessage, false))
                        }
                    }

                    messageText = ""
                }
            }
        )
    }
}

@Composable
private fun ChatMessages(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            ChatMessageItem(message = message)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (message.isUser)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = message.timestamp.format(DateTimeFormatter.ofPattern("h:mm a")),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Type your message...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                maxLines = 3,
                singleLine = false
            )

            Button(
                onClick = onSendMessage,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Send")
            }
        }
    }
}