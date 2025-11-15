package com.example.wear.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToScreen: (String) -> Unit,
    settings: com.example.wear.data.settings.AppSettings = com.example.wear.data.settings.AppSettings()
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var heartRate by remember { mutableIntStateOf(68) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)

            // Simulate heart rate variation (65-72 BPM)
            heartRate = (65..72).random()
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(42.dp))

            // Time Display
            Text(
                text = currentTime,
                style = MaterialTheme.typography.display1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )

            Text(
                text = currentDate,
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Heart Rate Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "$heartRate BPM",
                    style = MaterialTheme.typography.title1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    fontSize = 18.sp
                )
                Text(
                    text = "Heart Rate",
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

        }

        // Circular Action Buttons positioned around screen edge
        CircularActionButtons(
            onNavigateToScreen = onNavigateToScreen,
            enabledComplications = settings.enabledComplications.toList(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CircularActionButtons(
    onNavigateToScreen: (String) -> Unit,
    enabledComplications: List<com.example.wear.data.settings.ComplicationFeature>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenSize = configuration.screenWidthDp.dp
    val buttonSize = 30.dp
    val radius = (screenSize / 2) - (buttonSize / 2) - 4.dp // Position very close to edge

    // Map complications to action buttons with proper ordering
    val sortedComplications = enabledComplications.sortedBy { complication ->
        when (complication) {
            com.example.wear.data.settings.ComplicationFeature.SETTINGS -> 0
            com.example.wear.data.settings.ComplicationFeature.ALL_MEDS -> 1
            com.example.wear.data.settings.ComplicationFeature.HISTORY -> 2
            com.example.wear.data.settings.ComplicationFeature.MAINTENANCE -> 3
            com.example.wear.data.settings.ComplicationFeature.EMERGENCY -> 4
            com.example.wear.data.settings.ComplicationFeature.VITALS -> 5
            com.example.wear.data.settings.ComplicationFeature.UPCOMING -> 6
        }
    }

    // Calculate angles for enabled complications
    val actions = sortedComplications.mapIndexed { index, complication ->
        val angle = if (sortedComplications.size == 1) {
            0f // Single button at top
        } else {
            // Distribute evenly around circle, starting from top right and going clockwise
            val step = 360f / sortedComplications.size
            (360f - (step * index)) % 360f
        }

        val icon = when (complication) {
            com.example.wear.data.settings.ComplicationFeature.SETTINGS -> Icons.Default.Settings
            com.example.wear.data.settings.ComplicationFeature.ALL_MEDS -> Icons.Default.LocalPharmacy
            com.example.wear.data.settings.ComplicationFeature.HISTORY -> Icons.Default.History
            com.example.wear.data.settings.ComplicationFeature.MAINTENANCE -> Icons.Default.Refresh
            com.example.wear.data.settings.ComplicationFeature.EMERGENCY -> Icons.Default.Warning
            com.example.wear.data.settings.ComplicationFeature.VITALS -> Icons.Default.Favorite
            com.example.wear.data.settings.ComplicationFeature.UPCOMING -> Icons.Default.Notifications
        }

        ActionButton(complication.displayName, icon, angle)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        actions.forEach { action ->
            val angleRad = Math.toRadians(action.angle.toDouble())
            val x = (radius.value * cos(angleRad)).dp
            val y = (radius.value * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.primary,
                        shape = CircleShape
                    )
                    .clickable { onNavigateToScreen(action.title) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

data class ActionButton(
    val title: String,
    val icon: ImageVector,
    val angle: Float
)