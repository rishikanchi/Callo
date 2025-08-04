package com.example.callo.database

import android.util.Log
import com.example.callo.model.Event
import com.example.callo.model.Habit
import com.example.callo.model.Task
import com.example.callo.model.User
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from

class SupabaseManager {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://phlarqpxzrtgawxxidds.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBobGFycXB4enJ0Z2F3eHhpZGRzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzEzNDIwNzUsImV4cCI6MjA0NjkxODA3NX0.s4NdHz_ef-hx8dJRi_xtTTALlOuQmmMiejx1cdBUQFs"
    ) {
        install(Postgrest)
        install(Auth)
    }

    suspend fun createUser(user: User): Int {
        val result = supabase.from("users").insert(user)
        val output = this.getUserByEmail(user.email)!!.id
        return output
    }

    suspend fun getUser(id: Int): User? {
        return try {
            supabase.from("users")
                .select()
                .decodeList<User>()
                .find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            supabase.from("users")
                .select()
                .decodeList<User>()
                .find { it.email == email }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(id: Int, user: User): Boolean {
        return try {
            supabase.from("users")
                .update(
                    mapOf(
                        "name" to user.name,
                        "email" to user.email,
                        "password" to user.password
                    )
                )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return try {
            supabase.from("users").delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createEvent(event: Event, userId: Int): Int? {
        val result = supabase.from("events").insert(event.copy(userId = userId))
        return result.decodeSingle<Event>().id
    }

    suspend fun getEvent(id: Int): Event? {
        return try {
            supabase.from("events")
                .select()
                .decodeList<Event>()
                .find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserEvents(userId: Int): List<Event> {
        return try {
            supabase.from("events")
                .select()
                .decodeList<Event>()
                .filter { it.userId == userId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateEvent(id: Int, event: Event): Boolean {
        return try {
            supabase.from("events")
                .update(
                    mapOf(
                        "title" to event.title,
                        "description" to event.description,
                        "start_time" to event.startTime,
                        "end_time" to event.endTime
                    )
                )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteEvent(id: Int): Boolean {
        return try {
            supabase.from("events").delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createTask(task: Task, userId: Int): Int? {
        val result = supabase.from("tasks").insert(task.copy(userId = userId))
        return result.decodeSingle<Task>().id
    }

    suspend fun getTask(id: Int): Task? {
        return try {
            supabase.from("tasks")
                .select()
                .decodeList<Task>()
                .find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserTasks(userId: Int): List<Task> {
        return try {
            supabase.from("tasks")
                .select()
                .decodeList<Task>()
                .filter { it.userId == userId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateTask(id: Int, task: Task): Boolean {
        return try {
            supabase.from("tasks")
                .update(
                    mapOf(
                        "title" to task.title,
                        "due_date" to task.dueDate,
                        "is_completed" to task.isCompleted
                    )
                )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTask(id: Int): Boolean {
        return try {
            supabase.from("tasks").delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createHabit(habit: Habit, userId: Int): Int? {
        val result = supabase.from("habits").insert(habit.copy(userId = userId))
        return result.decodeSingle<Habit>().id
    }

    suspend fun getHabit(id: Int): Habit? {
        return try {
            supabase.from("habits")
                .select()
                .decodeList<Habit>()
                .find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserHabits(userId: Int): List<Habit> {
        return try {
            supabase.from("habits")
                .select()
                .decodeList<Habit>()
                .filter { it.userId == userId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateHabit(id: Int, habit: Habit): Boolean {
        return try {
            supabase.from("habits")
                .update(
                    mapOf(
                        "title" to habit.title,
                        "dates_completed" to habit.datesCompleted
                    )
                )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteHabit(id: Int): Boolean {
        return try {
            supabase.from("habits").delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addEventToUser(userId: Int, eventId: Int): Boolean {
        return try {
            val event = getEvent(eventId)
            event?.let {
                updateEvent(eventId, it.copy(userId = userId))
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addTaskToUser(userId: Int, taskId: Int): Boolean {
        return try {
            val task = getTask(taskId)
            task?.let {
                updateTask(taskId, it.copy(userId = userId))
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addHabitToUser(userId: Int, habitId: Int): Boolean {
        return try {
            val habit = getHabit(habitId)
            habit?.let {
                updateHabit(habitId, it.copy(userId = userId))
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}