package com.example.callo.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface CalendarItem {
    val id: Int
    val title: String
    val userId: Int
}