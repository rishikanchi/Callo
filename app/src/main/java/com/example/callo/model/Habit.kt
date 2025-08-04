package com.example.callo.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    override val id: Int,
    override val title: String,
    override val userId: Int,
    val datesCompleted: List<LocalDate> = emptyList()
) : CalendarItem {
    fun isCompletedOn(date: LocalDate): Boolean {
        return datesCompleted.contains(date)
    }
}