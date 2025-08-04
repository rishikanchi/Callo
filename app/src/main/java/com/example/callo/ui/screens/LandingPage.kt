package com.example.callo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.MainScreen
import com.example.callo.viewmodel.MainViewModel
import com.example.compose.CalloTheme

@Composable
fun LandingScreen(modifier: Modifier = Modifier) {
    var viewModel: MainViewModel = viewModel()

    var currentScreen by remember { mutableStateOf("landing") }

    when (currentScreen) {
        "landing" -> {
            CalloTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome to Callo,",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "your AI-driven productivity app.",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { currentScreen = "signup" },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Sign up")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { currentScreen = "login" },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Log in")
                    }
                }
            }
        }
        "signup" -> {
            SignUpScreen(
                onNavigateToLogin = { currentScreen = "login" },
                onNavigateToMain = { currentScreen = "main" }
            )
        }
        "login" -> {
            LoginScreen(
                onNavigateToSignUp = { currentScreen = "signup" },
                onLoginSuccess = { currentScreen = "main" }
            )
        }
        "main" -> {
            MainScreen(
                onNavigateToLanding = { currentScreen = "landing" }
            )
        }
    }
}