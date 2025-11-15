package com.example.wear.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

@Composable
fun PlaceholderScreen(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C853),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .size(120.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.caption1
                )
            }
        }
    }
}


@Composable
fun AllMedsScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("All Medications", onBackClick)
}

@Composable
fun HistoryScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("History", onBackClick)
}

@Composable
fun MaintenanceScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Maintenance", onBackClick)
}

@Composable
fun EmergencyScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Emergency", onBackClick)
}

@Composable
fun VitalsScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Vitals", onBackClick)
}

@Composable
fun UpcomingScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Upcoming", onBackClick)
}