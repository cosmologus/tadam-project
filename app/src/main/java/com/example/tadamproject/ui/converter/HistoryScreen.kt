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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.luminance
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

@Composable
fun HistoryScreen(
    viewModel: ConverterViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val groupedHistory = uiState.history.groupBy { historySectionLabel(it.timestamp) }
    val colors = historyColors()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            HistoryHeader(colors = colors, onBack = onBack)
        }
    ) { innerPadding ->
        if (uiState.history.isEmpty()) {
            EmptyHistoryScreen(
                colors = colors,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colors.background)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colors.background),
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
                            colors = colors,
                            conversions = conversions
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeader(
    colors: HistoryColors,
    onBack: () -> Unit
) {
    Surface(
        color = colors.headerBackground,
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
                    tint = colors.icon,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "History",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp),
                color = colors.textPrimary,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyHistoryScreen(
    colors: HistoryColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TODAY",
            color = colors.textSection,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "No conversions yet",
                modifier = Modifier.padding(horizontal = 34.dp, vertical = 34.dp),
                color = colors.textMuted,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistorySection(
    title: String,
    colors: HistoryColors,
    conversions: List<ConversionHistory>
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 14.dp),
            color = colors.textSection,
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
                    ambientColor = colors.shadow,
                    spotColor = colors.shadow
                ),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                conversions.forEach { conversion ->
                    HistoryRow(conversion = conversion, colors = colors)
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    conversion: ConversionHistory,
    colors: HistoryColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${conversion.fromCurrency} to ${conversion.toCurrency}",
                color = colors.textPrimary,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formatHistoryTime(conversion.timestamp),
                color = colors.textMuted,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${formatHistoryMoney(conversion.result)} ${conversion.toCurrency}",
                color = if (isToday(conversion.timestamp)) colors.accent else colors.textPrimary,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${formatHistoryMoney(conversion.amount)} ${conversion.fromCurrency}",
                color = colors.textMuted,
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

private data class HistoryColors(
    val background: Color,
    val headerBackground: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSection: Color,
    val icon: Color,
    val accent: Color,
    val shadow: Color
)

@Composable
private fun historyColors(): HistoryColors {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (isDark) {
        HistoryColors(
            background = Color(0xFF111827),
            headerBackground = Color(0xFF0F172A),
            cardBackground = Color(0xFF1F2937),
            textPrimary = Color(0xFFF8FAFC),
            textMuted = Color(0xFFCBD5E1),
            textSection = Color(0xFF9CA3AF),
            icon = Color(0xFFE5E7EB),
            accent = Color(0xFFA7A1FF),
            shadow = Color(0x33000000)
        )
    } else {
        HistoryColors(
            background = Color(0xFFF4F5F7),
            headerBackground = Color.White,
            cardBackground = Color.White,
            textPrimary = Color(0xFF161B26),
            textMuted = Color(0xFF667085),
            textSection = Color(0xFF98A2B3),
            icon = Color(0xFF475467),
            accent = Color(0xFF5F5599),
            shadow = Color(0x10000000)
        )
    }
}
