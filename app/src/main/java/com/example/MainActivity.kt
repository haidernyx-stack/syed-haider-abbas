package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClipForgeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ClipForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentTab by remember { mutableStateOf("home") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 0.dp,
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            val navItemColors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianSurfaceLight,
                                selectedTextColor = Color(0xFFE8DEF8),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = Color(0xFFE8DEF8)
                            )

                            NavigationBarItem(
                                selected = currentTab == "home",
                                onClick = { currentTab = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                colors = navItemColors
                            )
                            NavigationBarItem(
                                selected = currentTab == "projects",
                                onClick = { currentTab = "projects" },
                                icon = { Icon(Icons.Default.List, contentDescription = "Projects") },
                                label = { Text("Projects", fontSize = 11.sp) },
                                colors = navItemColors
                            )
                            NavigationBarItem(
                                selected = currentTab == "workstation",
                                onClick = { currentTab = "workstation" },
                                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Clipper Studio") },
                                label = { Text("Studio", fontSize = 11.sp) },
                                colors = navItemColors
                            )
                            NavigationBarItem(
                                selected = currentTab == "analytics",
                                onClick = { currentTab = "analytics" },
                                icon = { Icon(Icons.Default.Star, contentDescription = "Analytics") },
                                label = { Text("Analytics", fontSize = 11.sp) },
                                colors = navItemColors
                            )
                            NavigationBarItem(
                                selected = currentTab == "settings",
                                onClick = { currentTab = "settings" },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings", fontSize = 11.sp) },
                                colors = navItemColors
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentTab) {
                            "home" -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToWorkstation = { currentTab = "workstation" }
                            )
                            "projects" -> ProjectsScreen(
                                viewModel = viewModel,
                                onWorkstationRequested = { currentTab = "workstation" }
                            )
                            "workstation" -> ClipperStudioScreen(
                                viewModel = viewModel
                            )
                            "analytics" -> AnalyticsScreen(
                                viewModel = viewModel
                            )
                            "settings" -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

