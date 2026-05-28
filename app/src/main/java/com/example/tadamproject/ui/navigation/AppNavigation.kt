package com.example.tadamproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tadamproject.ui.AppViewModelProvider
import com.example.tadamproject.ui.converter.ConverterScreen
import com.example.tadamproject.ui.converter.ConverterViewModel
import com.example.tadamproject.ui.converter.HistoryScreen
import com.example.tadamproject.ui.settings.SettingsScreen
import com.example.tadamproject.ui.settings.SettingsViewModel

const val CONVERTER_ROUTE = "converter"
const val HISTORY_ROUTE = "history"
const val SETTINGS_ROUTE = "settings"

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CONVERTER_ROUTE,
        modifier = modifier
    ) {
        composable(CONVERTER_ROUTE) {
            val viewModel: ConverterViewModel = viewModel(factory = AppViewModelProvider.Factory)
            ConverterScreen(
                viewModel = viewModel,
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                onOpenHistory = { navController.navigate(HISTORY_ROUTE) }
            )
        }

        composable(HISTORY_ROUTE) {
            val viewModel: ConverterViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SETTINGS_ROUTE) {
            val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
