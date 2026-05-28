package com.example.tadamproject.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tadamproject.model.ConversionHistory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val currencies = listOf("USD", "EUR", "GBP", "RON", "JPY", "CHF")
private val screenBackground = Color(0xFFF2F3F7)
private val cardBackground = Color.White
private val fieldBackground = Color(0xFFF8F9FB)
private val textPrimary = Color(0xFF1F2937)
private val textStrong = Color(0xFF111827)
private val textMuted = Color(0xFF6B7280)
private val textSoft = Color(0xFF9CA3AF)
private val accentPurple = Color(0xFF6757A7)

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = screenBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(screenBackground),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Header(onOpenSettings = onOpenSettings)
            }

            item {
                ConversionCard(
                    uiState = uiState,
                    onFromCurrencySelected = viewModel::updateFromCurrency,
                    onToCurrencySelected = viewModel::updateToCurrency,
                    onAmountChange = viewModel::updateAmount,
                    onSwapCurrencies = viewModel::swapCurrencies,
                    onConvert = viewModel::convert
                )
            }

            item {
                ResultSection(uiState = uiState)
            }

            item {
                RecentSection(history = uiState.history.take(3))
            }
        }
    }
}

@Composable
private fun Header(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 28.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Currency Exchange",
            color = textPrimary,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        IconButton(onClick = onOpenSettings) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color(0xFF4B5563),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun ConversionCard(
    uiState: ConverterUiState,
    onFromCurrencySelected: (String) -> Unit,
    onToCurrencySelected: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSwapCurrencies: () -> Unit,
    onConvert: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x12000000)),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            CurrencyPairSelector(
                fromCurrency = uiState.fromCurrency,
                toCurrency = uiState.toCurrency,
                onFromCurrencySelected = onFromCurrencySelected,
                onToCurrencySelected = onToCurrencySelected,
                onSwapCurrencies = onSwapCurrencies
            )

            AmountInput(
                amount = uiState.amount,
                error = uiState.error,
                onAmountChange = onAmountChange
            )

            Button(
                onClick = onConvert,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentPurple,
                    contentColor = Color.White,
                    disabledContainerColor = accentPurple.copy(alpha = 0.55f),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Convert",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyPairSelector(
    fromCurrency: String,
    toCurrency: String,
    onFromCurrencySelected: (String) -> Unit,
    onToCurrencySelected: (String) -> Unit,
    onSwapCurrencies: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = fieldBackground,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyDropdown(
                selectedCurrency = fromCurrency,
                onCurrencySelected = onFromCurrencySelected,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onSwapCurrencies,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(5.dp, CircleShape)
                    .background(cardBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap currencies",
                    tint = accentPurple,
                    modifier = Modifier.size(30.dp)
                )
            }

            CurrencyDropdown(
                selectedCurrency = toCurrency,
                onCurrencySelected = onToCurrencySelected,
                modifier = Modifier.weight(1f),
                alignment = Alignment.End
            )
        }
    }
}

@Composable
private fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(if (alignment == Alignment.End) Alignment.CenterEnd else Alignment.CenterStart)
                .clickable { expanded = true }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCurrency,
                color = textPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = textSoft,
                modifier = Modifier.size(26.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = currency,
                            fontWeight = if (currency == selectedCurrency) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    error: String?,
    onAmountChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Amount",
            color = textMuted,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            textStyle = TextStyle(
                color = textStrong,
                fontSize = 54.sp,
                lineHeight = 60.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (amount.isBlank()) {
                        Text(
                            text = "0",
                            color = textSoft,
                            fontSize = 54.sp,
                            lineHeight = 60.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ResultSection(uiState: ConverterUiState) {
    val amount = uiState.amount.toDoubleOrNull()
    val result = uiState.result.toDoubleOrNull()
    val headline = if (result != null) {
        "${formatMoney(result)} ${uiState.toCurrency}"
    } else {
        "-- ${uiState.toCurrency}"
    }
    val summary = if (amount != null && result != null) {
        "${formatMoney(amount)} ${uiState.fromCurrency} ="
    } else {
        "Converted amount"
    }
    val rate = if (amount != null && amount > 0.0 && result != null) {
        "Mid-market rate: 1 ${uiState.fromCurrency} = ${formatRate(result / amount)} ${uiState.toCurrency}"
    } else {
        "Mid-market rate appears after conversion"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = summary,
            color = textMuted,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = headline,
            color = textStrong,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = textSoft,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = rate,
                color = textSoft,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecentSection(history: List<ConversionHistory>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent",
                    color = textPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "See All",
                    color = accentPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (history.isEmpty()) {
                EmptyHistoryItem()
            } else {
                history.forEach { conversion ->
                    HistoryItem(conversion = conversion)
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryItem() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = fieldBackground,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "No conversions yet",
            modifier = Modifier.padding(22.dp),
            color = textMuted,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun HistoryItem(conversion: ConversionHistory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = fieldBackground,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${conversion.fromCurrency} to ${conversion.toCurrency}",
                    color = textPrimary,
                    fontSize = 19.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRelativeTimestamp(conversion.timestamp),
                    color = textMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatMoney(conversion.result)} ${conversion.toCurrency}",
                    color = textPrimary,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatMoney(conversion.amount)} ${conversion.fromCurrency}",
                    color = textMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatMoney(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(value)
}

private fun formatRate(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    val formatter = DecimalFormat("#,##0.####", symbols)
    return formatter.format(value)
}

private fun formatRelativeTimestamp(timestamp: Long): String {
    val date = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(date, today) -> {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            "Today, ${formatter.format(Date(timestamp))}"
        }

        isSameDay(date, yesterday) -> "Yesterday"

        else -> {
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

private fun isSameDay(first: Calendar, second: Calendar): Boolean {
    return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
}
