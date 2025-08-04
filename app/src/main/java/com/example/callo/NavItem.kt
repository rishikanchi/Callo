package com.example.callo

import androidx.compose.ui.graphics.vector.ImageVector

/*
This class will represent each option on the bottom navbar in MainScreen.kt
 */
data class NavItem(
    val label: String,
    val icon: ImageVector
)
