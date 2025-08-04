package com.example.callo.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class Event(
    override val id: Int,
    override val title: String,
    override val userId: Int,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) : CalendarItem