package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.MedicationRepository
import com.example.timelycare.ui.theme.TimelyCareTextPrimary
import com.example.timelycare.ui.theme.TimelyCareTextSecondary

@Composable
fun DashboardScreen(
    onHealthMetricClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val medicationRepository = remember { MedicationRepository.getInstance(context) }
    val medications by medicationRepository.medications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Health Metrics Section
        HealthMetricsSection(
            onMetricClick = onHealthMetricClick
        )

        // Today's Schedule Section
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Today's Schedule",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )

            if (medications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No medications scheduled for today",
                        fontSize = 16.sp,
                        color = TimelyCareTextSecondary
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    medications.forEach { medication ->
                        TodayMedicationCard(medication = medication)
                    }
                }
            }
        }
    }
}