package com.example.timelycare.ui.screens.medications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.Medication
import com.example.timelycare.data.MedicationRepository
import com.example.timelycare.ui.theme.*

@Composable
fun MedicationsScreen(onAddClick: () -> Unit, onEditClick: (Medication) -> Unit) {
    val repository = remember { MedicationRepository.getInstance() }
    val medications by repository.medications.collectAsStateWithLifecycle()

    if (medications.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "No medications added yet.",
                    fontSize = 16.sp,
                    color = TimelyCareTextSecondary
                )

                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimelyCareBlue
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Add Medicine",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { medication ->
                    MedicationCard(
                        medication = medication,
                        onEdit = { onEditClick(medication) },
                        onDelete = { repository.deleteMedication(medication.id) }
                    )
                }
            }

            // Add Medicine Button at bottom
            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TimelyCareBlue
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Add Medicine",
                    color = TimelyCareWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}