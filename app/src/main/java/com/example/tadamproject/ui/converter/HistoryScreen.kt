package com.example.tadamproject.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

private val historyBackground = Color(0xFFF4F5F7)
private val cardBackground = Color.White
private val textPrimary = Color(0xFF161B26)
private val textMuted = Color(0xFF667085)
private val textSection = Color(0xFF98A2B3)
private val iconTint = Color(0xFF475467)
private val accentPurple = Color(0xFF5F5599)

@Composable
fun HistoryScreen(
    viewModel: ConverterViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val groupedHistory = uiState.history.groupBy { historySectionLabel(it.timestamp) }

    Scaffold(
        containerColor = historyBackground,
        topBar = {
            HistoryHeader(onBack = onBack)
        }
    ) { innerPadding ->
        if (uiState.history.isEmpty()) {
            EmptyHistoryScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(historyBackground)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(historyBackground),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp,
                    top = 30.dp,
                    end = 24.dp,
                    bottom = 36.dp
                )
            ) {
                groupedHistory.forEach { (section, conversions) ->
                    item(key = section) {
                        HistorySection(
                            title = section,
                            conversions = conversions
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeader(onBack: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = iconTint,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "History",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp),
                color = textPrimary,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyHistoryScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TODAY",
            color = textSection,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "No conversions yet",
                modifier = Modifier.padding(horizontal = 34.dp, vertical = 34.dp),
                color = textMuted,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistorySection(
    title: String,
    conversions: List<ConversionHistory>
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 14.dp),
            color = textSection,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Color(0x10000000),
                    spotColor = Color(0x08000000)
                ),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                conversions.forEach { conversion ->
                    HistoryRow(conversion = conversion)
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(conversion: ConversionHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${conversion.fromCurrency} to ${conversion.toCurrency}",
                color = textPrimary,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formatHistoryTime(conversion.timestamp),
                color = textMuted,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${formatHistoryMoney(conversion.result)} ${conversion.toCurrency}",
                color = if (isToday(conversion.timestamp)) accentPurple else textPrimary,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${formatHistoryMoney(conversion.amount)} ${conversion.fromCurrency}",
                color = textMuted,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun historySectionLabel(timestamp: Long): String {
    val date = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(date, today) -> "TODAY"
        isSameDay(date, yesterday) -> "YESTERDAY"
        else -> {
            val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
            formatter.format(Date(timestamp)).uppercase(Locale.US)
        }
    }
}

private fun formatHistoryTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.US)
    return formatter.format(Date(timestamp))
}

private fun formatHistoryMoney(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(value)
}

private fun isToday(timestamp: Long): Boolean {
    val date = Calendar.getInstance().apply { timeInMillis = timestamp }
    return isSameDay(date, Calendar.getInstance())
}

private fun isSameDay(first: Calendar, second: Calendar): Boolean {
    return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
}
