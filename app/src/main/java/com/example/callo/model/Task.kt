package com.example.callo.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    override val id: Int,
    override val title: String,
    override val userId: Int,
    val dueDate: LocalDate,
    val isCompleted: Boolean = false
) : CalendarItem