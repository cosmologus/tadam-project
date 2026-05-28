package com.example.tadamproject.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tadamproject.data.CurrencyRepository
import com.example.tadamproject.model.ConversionHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConverterUiState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val amount: String = "",
    val result: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<ConversionHistory> = emptyList()
)

class ConverterViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getConversionHistory().collect { history ->
                _uiState.update { currentState ->
                    currentState.copy(history = history)
                }
            }
        }
    }

    fun updateFromCurrency(currency: String) {
        _uiState.update { currentState ->
            currentState.copy(fromCurrency = currency, error = null)
        }
    }

    fun updateToCurrency(currency: String) {
        _uiState.update { currentState ->
            currentState.copy(toCurrency = currency, error = null)
        }
    }

    fun swapCurrencies() {
        _uiState.update { currentState ->
            currentState.copy(
                fromCurrency = currentState.toCurrency,
                toCurrency = currentState.fromCurrency,
                result = "",
                error = null
            )
        }
    }

    fun updateAmount(amount: String) {
        val isValidInput = amount.all { character ->
            character.isDigit() || character == '.'
        } && amount.count { character -> character == '.' } <= 1

        if (!isValidInput) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(amount = amount, error = null)
        }
    }

    fun convert() {
        val currentState = _uiState.value
        val amountValue = currentState.amount.toDoubleOrNull()

        if (amountValue == null) {
            _uiState.update { state ->
                state.copy(error = "Enter a valid amount")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, error = null)
            }

            try {
                val convertedAmount = repository.convert(
                    from = currentState.fromCurrency,
                    to = currentState.toCurrency,
                    amount = amountValue
                )

                val conversion = ConversionHistory(
                    fromCurrency = currentState.fromCurrency,
                    toCurrency = currentState.toCurrency,
                    amount = amountValue,
                    result = convertedAmount
                )

                repository.saveConversion(conversion)

                _uiState.update { state ->
                    state.copy(
                        result = convertedAmount.toString(),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = "Error")
                }
            }
        }
    }
}
