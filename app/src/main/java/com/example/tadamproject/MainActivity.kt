package com.example.tadamproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.tadamproject.ui.navigation.AppNavigation
import com.example.tadamproject.ui.theme.TADAMProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as TadamApplication).container

        setContent {
            val isDarkMode by container.themePreferences.isDarkMode.collectAsState(initial = false)

            TADAMProjectTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }
}
