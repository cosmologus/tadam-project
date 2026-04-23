package com.example.tadamproject.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tadamproject.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false
)

class SettingsViewModel(
    private val themePreferences: ThemePreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferences.isDarkMode.collect { isDarkMode ->
                _uiState.update { currentState ->
                    currentState.copy(isDarkMode = isDarkMode)
                }
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(enabled)
        }
    }
}
