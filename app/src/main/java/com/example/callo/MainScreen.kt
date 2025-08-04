package com.example.callo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.model.User
import com.example.callo.ui.screens.*
import com.example.callo.viewmodel.MainViewModel
import com.example.compose.CalloTheme
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreen(onNavigateToLanding: () -> Unit, modifier: Modifier = Modifier) {
    var viewModel: MainViewModel = viewModel()

    CalloTheme {
        val navItemList = listOf(
            NavItem("Home", Icons.Default.Home),
            NavItem("Calendar", Icons.Default.CalendarMonth),
            NavItem("AI", Icons.Default.Assistant)
        )

        var selectedIndex by remember { mutableIntStateOf(0) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    navItemList.forEachIndexed{ index, navItem ->
                        NavigationBarItem(
                            selected = (selectedIndex == index),
                            onClick = { selectedIndex = index },
                            icon = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                )
                            },
                            label = {
                                Text(text = navItem.label)
                            }
                        )
                    }
                }
            }
        ) {innerPadding ->
            ContentScreen(selectedIndex, onIndexChange = { selectedIndex = it }, onNavigateToLanding = onNavigateToLanding, modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ContentScreen(index: Int, onIndexChange: (Int) -> Unit, onNavigateToLanding: () -> Unit, modifier: Modifier = Modifier) {
    var viewModel: MainViewModel = viewModel()

    when (index) {
        0 -> HomeScreen(
            onNavigateToAddEvent = { onIndexChange(3) },
            onNavigateToAddTask = { onIndexChange(4) },
            onNavigateToAddHabit = { onIndexChange(5) },
            onNavigateToLanding = onNavigateToLanding,
            modifier = modifier.fillMaxSize()
        )
        1 -> CalendarScreen(modifier = modifier.fillMaxSize())
        2 -> AIScreen(modifier = modifier.fillMaxSize())
        3 -> CreateEventScreen(onNavigateBack = { onIndexChange(0) })
        4 -> CreateTaskScreen(onNavigateBack = { onIndexChange(0) })
        5 -> CreateHabitScreen(onNavigateBack = { onIndexChange(0) })
    }
}