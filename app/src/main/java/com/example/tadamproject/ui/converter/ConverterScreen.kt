package com.example.tadamproject.ui.converter

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tadamproject.model.ConversionHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.input.KeyboardType

private val currencies = listOf("USD", "EUR", "GBP", "RON", "JPY", "CHF")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency Exchange") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CurrencySelector(
                    title = "From",
                    selectedCurrency = uiState.fromCurrency,
                    onCurrencySelected = viewModel::updateFromCurrency
                )
            }

            item {
                CurrencySelector(
                    title = "To",
                    selectedCurrency = uiState.toCurrency,
                    onCurrencySelected = viewModel::updateToCurrency
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text("Amount") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    // Decimal keyboard limits input to number-friendly characters.
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            item {
                Button(
                    onClick = viewModel::convert,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Convert")
                }
            }

            item {
                ResultSection(uiState = uiState)
            }

            item {
                Text(
                    text = "Recent Conversions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.history.isEmpty()) {
                item {
                    Text(
                        text = "No conversions yet",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(uiState.history, key = { it.id }) { item ->
                    HistoryItem(conversion = item)
                }
            }
        }
    }
}

@Composable
private fun CurrencySelector(
    title: String,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)

            currencies.forEach { currency ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = selectedCurrency == currency,
                        onClick = { onCurrencySelected(currency) }
                    )
                    Text(
                        text = currency,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSection(uiState: ConverterUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text(text = uiState.error)
                }

                uiState.result.isNotBlank() -> {
                    Text(text = "Result: ${uiState.result} ${uiState.toCurrency}")
                }

                else -> {
                    Text(text = "Result will appear here")
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(conversion: ConversionHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${conversion.fromCurrency} -> ${conversion.toCurrency}",
                fontWeight = FontWeight.Bold
            )
            Text(text = "${conversion.amount} = ${conversion.result}")
            Text(text = formatTimestamp(conversion.timestamp))
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
