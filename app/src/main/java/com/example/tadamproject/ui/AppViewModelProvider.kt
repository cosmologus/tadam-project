package com.example.tadamproject.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tadamproject.TadamApplication
import com.example.tadamproject.ui.converter.ConverterViewModel
import com.example.tadamproject.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ConverterViewModel(tadamApplication().container.currencyRepository)
        }
        initializer {
            SettingsViewModel(tadamApplication().container.themePreferences)
        }
    }
}

private fun CreationExtras.tadamApplication(): TadamApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TadamApplication
}
