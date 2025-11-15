package com.example.wear.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.wear.data.settings.*
import com.example.wear.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    settingsRepository: com.example.wear.data.settings.SettingsRepository
) {
    val viewModel: SettingsViewModel = remember { SettingsViewModel(settingsRepository) }
    val settings by viewModel.settings.collectAsState()
    val listState = rememberScalingLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 32.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 60.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.title2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Text Size Section
            item {
                Text(
                    text = "Text Size",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            item {
                TextSizeSelector(
                    selectedSize = settings.textSize,
                    onSizeSelected = { viewModel.updateTextSize(it) }
                )
            }

            // Accent Color Section
            item {
                Text(
                    text = "Accent Color",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            item {
                AccentColorPicker(
                    selectedColor = settings.accentColor,
                    onColorSelected = { viewModel.updateAccentColor(it) }
                )
            }

            // Watch Type Section
            item {
                Text(
                    text = "Watch Type",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            item {
                WatchTypeSelector(
                    selectedType = settings.watchType,
                    onTypeSelected = { viewModel.updateWatchType(it) }
                )
            }

            // Dark Mode Section
            item {
                DarkModeToggle(
                    isDarkMode = settings.isDarkMode,
                    onToggle = { viewModel.updateDarkMode(it) }
                )
            }

            // Complications Section
            item {
                Text(
                    text = "Complications",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            items(ComplicationFeature.values().toList()) { feature ->
                ComplicationToggle(
                    feature = feature,
                    isEnabled = feature in settings.enabledComplications,
                    onToggle = { enabled -> viewModel.toggleComplication(feature, enabled) }
                )
            }
        }

        // Floating Back Button
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .size(44.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TextSizeSelector(
    selectedSize: TextSize,
    onSizeSelected: (TextSize) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextSize.values().forEach { size ->
            val isSelected = size == selectedSize
            Box(
                modifier = Modifier
                    .size(width = 52.dp, height = 28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) MaterialTheme.colors.primary
                        else MaterialTheme.colors.surface
                    )
                    .clickable { onSizeSelected(size) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = size.displayName,
                    style = MaterialTheme.typography.caption1,
                    color = if (isSelected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun AccentColorPicker(
    selectedColor: AccentColor,
    onColorSelected: (AccentColor) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row - Green, Blue, Purple
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(AccentColor.GREEN, AccentColor.BLUE, AccentColor.PURPLE).forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = color == selectedColor,
                    onSelected = { onColorSelected(color) }
                )
            }
        }

        // Second row - Red, Orange, Pink
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(AccentColor.RED, AccentColor.ORANGE, AccentColor.PINK).forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = color == selectedColor,
                    onSelected = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: AccentColor,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color.color)
            .clickable { onSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun WatchTypeSelector(
    selectedType: WatchType,
    onTypeSelected: (WatchType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WatchType.values().forEach { type ->
            val isSelected = type == selectedType
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) MaterialTheme.colors.primary
                        else MaterialTheme.colors.surface
                    )
                    .clickable { onTypeSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.caption1,
                    color = if (isSelected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DarkModeToggle(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dark Mode",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground
        )
        Switch(
            checked = isDarkMode,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun ComplicationToggle(
    feature: ComplicationFeature,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val icon = when (feature) {
        ComplicationFeature.SETTINGS -> Icons.Default.Settings
        ComplicationFeature.ALL_MEDS -> Icons.Default.LocalPharmacy
        ComplicationFeature.HISTORY -> Icons.Default.History
        ComplicationFeature.MAINTENANCE -> Icons.Default.Refresh
        ComplicationFeature.EMERGENCY -> Icons.Default.Warning
        ComplicationFeature.VITALS -> Icons.Default.Favorite
        ComplicationFeature.UPCOMING -> Icons.Default.Notifications
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = feature.displayName,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = feature.displayName,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = if (feature.isAlwaysEnabled) null else onToggle,
            enabled = !feature.isAlwaysEnabled
        )
    }
}