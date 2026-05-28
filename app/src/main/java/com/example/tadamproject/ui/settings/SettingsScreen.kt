package com.example.tadamproject.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = settingsColors(uiState.isDarkMode)

    Scaffold(
        containerColor = colors.background,
        topBar = {
            SettingsHeader(
                colors = colors,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
                .padding(horizontal = 24.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "APPEARANCE",
                color = colors.sectionText,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            ThemeSettingCard(
                isDarkMode = uiState.isDarkMode,
                colors = colors,
                onDarkModeChange = viewModel::setDarkMode
            )
        }
    }
}

@Composable
private fun SettingsHeader(
    colors: SettingsColors,
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
                text = "Settings",
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
private fun ThemeSettingCard(
    isDarkMode: Boolean,
    colors: SettingsColors,
    onDarkModeChange: (Boolean) -> Unit
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(colors.iconBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DarkMode,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "Dark Mode",
                    color = colors.textPrimary,
                    fontSize = 23.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = colors.accent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = colors.switchTrack,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

private data class SettingsColors(
    val background: Color,
    val headerBackground: Color,
    val cardBackground: Color,
    val iconBackground: Color,
    val sectionText: Color,
    val textPrimary: Color,
    val icon: Color,
    val accent: Color,
    val switchTrack: Color,
    val shadow: Color
)

private fun settingsColors(isDarkMode: Boolean): SettingsColors {
    return if (isDarkMode) {
        SettingsColors(
            background = Color(0xFF111827),
            headerBackground = Color(0xFF0F172A),
            cardBackground = Color(0xFF1F2937),
            iconBackground = Color(0xFF2E335E),
            sectionText = Color(0xFF9CA3AF),
            textPrimary = Color(0xFFF8FAFC),
            icon = Color(0xFFE5E7EB),
            accent = Color(0xFFA7A1FF),
            switchTrack = Color(0xFF4B5563),
            shadow = Color(0x33000000)
        )
    } else {
        SettingsColors(
            background = Color(0xFFF4F5F7),
            headerBackground = Color.White,
            cardBackground = Color.White,
            iconBackground = Color(0xFFEDEFFD),
            sectionText = Color(0xFF98A2B3),
            textPrimary = Color(0xFF161B26),
            icon = Color(0xFF475467),
            accent = Color(0xFF5F5599),
            switchTrack = Color(0xFFD9DCE3),
            shadow = Color(0x10000000)
        )
    }
}
