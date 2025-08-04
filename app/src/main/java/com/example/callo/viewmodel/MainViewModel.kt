package com.example.callo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callo.database.SupabaseManager
import com.example.callo.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

class MainViewModel : ViewModel() {
    val supabaseManager = SupabaseManager()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val demoEvents = listOf(
        Event(
            id = 1,
            title = "Run Code",
            userId = 1,
            description = "Run",
            startTime = LocalDateTime(2024, 12, 12, 10, 0),
            endTime = LocalDateTime(2024, 12, 12, 11, 0)
        ),
        Event(
            id = 2,
            title = "Lunch with Client",
            userId = 1,
            description = "Project discussion over lunch",
            startTime = LocalDateTime(2024, 12, 12, 12, 30),
            endTime = LocalDateTime(2024, 12, 12, 13, 30)
        ),
        Event(
            id = 3,
            title = "Project Review",
            userId = 1,
            description = "End of sprint review",
            startTime = LocalDateTime(2024, 12, 13, 14, 0),
            endTime = LocalDateTime(2024, 12, 13, 15, 30)
        )
    )

    private val demoTasks = listOf(
        Task(
            id = 1,
            title = "Prepare Presentation",
            userId = 1,
            dueDate = LocalDate(2024, 12, 13),
            isCompleted = false
        ),
        Task(
            id = 2,
            title = "Review Code",
            userId = 1,
            dueDate = LocalDate(2024, 12, 12),
            isCompleted = false
        ),
        Task(
            id = 3,
            title = "Update Documentation",
            userId = 1,
            dueDate = LocalDate(2024, 12, 14),
            isCompleted = false
        ),
        Task(
            id = 4,
            title = "Team Meeting Notes",
            userId = 1,
            dueDate = LocalDate(2024, 12, 12),
            isCompleted = false
        )
    )

    private val demoHabits = listOf(
        Habit(
            id = 1,
            title = "Morning Meditation",
            userId = 1,
            datesCompleted = listOf(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 1)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 2)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 5)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 8)
                ),
            )
        ),
        Habit(
            id = 3,
            title = "Read 30 Minutes",
            userId = 1,
            datesCompleted = listOf(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 1)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 2)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 23)
                ),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(
                    DatePeriod(days = 15)
                )
            )
        ),
        Habit(
            id = 2,
            title = "Exercise",
            userId = 1,
            datesCompleted = listOf()
        ),
    )

    private val _events = MutableStateFlow<List<Event>>(demoEvents)
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(demoTasks)
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _habits = MutableStateFlow<List<Habit>>(demoHabits)
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    fun updateUser(userId: Int) {
        viewModelScope.launch {
            try {
                val user = supabaseManager.getUser(userId)
                _currentUser.emit(user)
                Log.d(getCurrentUser()!!.name, "Updated user")
            } catch (e: Exception) {
                Log.d("updateUser", "Couldn't update user")
                // Handle error
                _uiState.value = UiState.Error(e.message ?: "Failed to update user")
            }
        }
    }
    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = User(
                    id = Random.nextInt(1, Int.MAX_VALUE),
                    name = name,
                    email = email,
                    password = password
                )
                val userId = supabaseManager.createUser(user)
                updateUser(userId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun login(email: String, password: String): Int {
        var output = 1
        runBlocking {
            try {
                _uiState.value = UiState.Loading
                val user = supabaseManager.getUserByEmail(email)

                when {
                    user == null -> _uiState.value = UiState.Error("User not found")
                    user.password != password -> _uiState.value = UiState.Error("Invalid password")
                    else -> {
                        _currentUser.value = user
                        _uiState.value = UiState.Success
                        output = 0 // Set output to 0 for success
                    }
                }
            } catch (e: Exception) {
                Log.d("Guess", "Guess")
                _uiState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
        return output
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = UiState.Initial
    }

    // Event
    fun createEvent(event: Event) {
        viewModelScope.launch {
            val currentEvents = _events.value.toMutableList()
            currentEvents.add(event)
            _events.emit(currentEvents)
        }
    }
    fun createTask(task: Task) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            currentTasks.add(task)
            _tasks.emit(currentTasks)
        }
    }
    fun createHabit(habit: Habit) {
        viewModelScope.launch {
            val currentHabits = _habits.value.toMutableList()
            currentHabits.add(habit)
            _habits.emit(currentHabits)
        }
    }

    fun updateEvent(updatedEvent: Event) {
        viewModelScope.launch {
            val currentEvents = _events.value.map { event ->
                if (event.id == updatedEvent.id) updatedEvent else event
            }
            _events.emit(currentEvents)
        }
    }
    fun updateTask(updatedTaskId: Int) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.map { task ->
                if (task.id == updatedTaskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else task
            }
            _tasks.emit(currentTasks)
        }
    }
    fun updateHabit(habitId: Int) {
        viewModelScope.launch {
            val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val currentHabits = _habits.value.map { habit ->
                if (habit.id == habitId) {
                    if (habit.isCompletedOn(currentDate)) {
                        habit.copy(
                            datesCompleted = habit.datesCompleted.filter { it != currentDate }
                        )
                    } else {
                        habit.copy(
                            datesCompleted = habit.datesCompleted + currentDate
                        )
                    }
                } else habit
            }
            _habits.emit(currentHabits)
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            val currentEvents = _events.value.filter { event ->
                event.id != eventId
            }
            _events.emit(currentEvents)
        }
    }

    fun getAllEvents(): List<Event> {
        return _events.value
    }
    fun getAllTasks(): List<Task> {
        return _tasks.value
    }
    fun getAllHabits(): List<Habit> {
        return _habits.value
    }

    /*
    Supabase Unused Functions
    // Event functions
    fun createEvent(title: String, description: String, startTime: LocalDateTime, endTime: LocalDateTime) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                val newEvent = Event(
                    id = 0, // ID will be assigned by Supabase
                    userId = userId,
                    title = title,
                    description = description,
                    startTime = startTime,
                    endTime = endTime
                )

                supabaseManager.createEvent(newEvent, userId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create event")
            }
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                supabaseManager.deleteEvent(eventId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete event")
            }
        }
    }

    // Task functions
    fun createTask(title: String, dueDate: LocalDate) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                val newTask = Task(
                    id = 0, // ID will be assigned by Supabase
                    userId = userId,
                    title = title,
                    dueDate = dueDate,
                    isCompleted = false
                )

                supabaseManager.createTask(newTask, userId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create task")
            }
        }
    }

    fun updateTaskCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                val task = supabaseManager.getTask(taskId)
                task?.let {
                    val updatedTask = it.copy(isCompleted = isCompleted)
                    supabaseManager.updateTask(taskId, updatedTask)
                    refreshUser(userId)
                }
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update task")
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                supabaseManager.deleteTask(taskId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete task")
            }
        }
    }

    // Habit functions
    fun createHabit(title: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                val newHabit = Habit(
                    id = 0, // ID will be assigned by Supabase
                    userId = userId,
                    title = title,
                    datesCompleted = emptyList()
                )

                supabaseManager.createHabit(newHabit, userId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create habit")
            }
        }
    }

    fun markHabitComplete(habitId: Int, date: LocalDate) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                val habit = supabaseManager.getHabit(habitId)
                habit?.let {
                    val updatedDates = it.datesCompleted.toMutableList()
                    if (!updatedDates.contains(date)) {
                        updatedDates.add(date)
                    }
                    val updatedHabit = it.copy(datesCompleted = updatedDates)
                    supabaseManager.updateHabit(habitId, updatedHabit)
                    refreshUser(userId)
                }
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to mark habit complete")
            }
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val userId = requireCurrentUserId()

                supabaseManager.deleteHabit(habitId)
                refreshUser(userId)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete habit")
            }
        }
    }

    // Data retrieval functions using User's IDs
    suspend fun getCurrentUserEvents(): List<Event> {
        val userId = requireCurrentUserId()
        return supabaseManager.getUserEvents(userId)
    }

    suspend fun getCurrentUserTasks(): List<Task> {
        val userId = requireCurrentUserId()
        return supabaseManager.getUserTasks(userId)
    }

    suspend fun getCurrentUserHabits(): List<Habit> {
        val userId = requireCurrentUserId()
        return supabaseManager.getUserHabits(userId)
    }

    // Helper functions
    private suspend fun refreshUser(userId: Int) {
        try {
            _currentUser.value = supabaseManager.getUser(userId)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Failed to refresh user data")
        }
    }

    private fun requireCurrentUserId(): Int {
        return currentUser.value?.id ?: throw IllegalStateException("No user logged in")
    }
     */
}

sealed class UiState {
    data object Initial : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String) : UiState()
}