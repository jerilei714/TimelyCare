package com.example.timelycare.ui.screens.medications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.Frequency
import com.example.timelycare.data.Medication
import com.example.timelycare.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = medication.dosage,
                        fontSize = 16.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    if (medication.medicationTimes.isNotEmpty()) {
                        Text(
                            text = "Times: ${medication.medicationTimes.joinToString(", ") {
                                it.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            }}",
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = "Frequency: ${formatFrequencyLabel(medication.frequency)}",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Button
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, TimelyCareBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TimelyCareBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete Button
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, Color(0xFFF44336)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatFrequencyLabel(frequency: Frequency): String {
    return when (frequency) {
        is Frequency.Daily -> "Daily"
        is Frequency.SpecificDays -> {
            if (frequency.days.isEmpty()) {
                "Custom"
            } else {
                frequency.days.joinToString(", ") { it.toString() }
            }
        }
    }
}