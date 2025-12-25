package com.seek.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.seek.app.ui.navigation.SeekNavHost

@Composable
fun SeekApp() {
    val navController = rememberNavController()
    SeekNavHost(navController = navController)
}
