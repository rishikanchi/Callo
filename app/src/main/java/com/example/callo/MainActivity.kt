package com.example.callo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callo.ui.screens.LandingScreen
import com.example.callo.viewmodel.MainViewModel
import com.example.compose.CalloTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://phlarqpxzrtgawxxidds.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBobGFycXB4enJ0Z2F3eHhpZGRzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzEzNDIwNzUsImV4cCI6MjA0NjkxODA3NX0.s4NdHz_ef-hx8dJRi_xtTTALlOuQmmMiejx1cdBUQFs"
) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalloTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LandingScreen(modifier = Modifier.padding(innerPadding))
               }
            }
        }
    }
}