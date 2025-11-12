package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.MedicationRepository
import com.example.timelycare.ui.theme.TimelyCareTextPrimary
import com.example.timelycare.ui.theme.TimelyCareTextSecondary

@Composable
fun DashboardScreen() {
    val repository = remember { MedicationRepository.getInstance() }
    val medications by repository.medications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Today's Schedule",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (medications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No medications scheduled for today",
                    fontSize = 16.sp,
                    color = TimelyCareTextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { medication ->
                    TodayMedicationCard(medication = medication)
                }
            }
        }
    }
}