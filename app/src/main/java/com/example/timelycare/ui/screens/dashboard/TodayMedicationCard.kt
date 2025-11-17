package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.Medication
import com.example.timelycare.data.MedicationTakenRepository
import com.example.timelycare.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayMedicationCard(
    medication: Medication,
    scheduledTime: LocalTime
) {
    val context = LocalContext.current
    val takenRepository = remember { MedicationTakenRepository.getInstance(context) }
    val takenRecords by takenRepository.takenRecords.collectAsStateWithLifecycle()

    val isTaken by remember(takenRecords) {
        derivedStateOf {
            takenRepository.isMedicationTaken(medication.id, scheduledTime)
        }
    }

    val borderColor = if (isTaken) Color(0xFF4CAF50) else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isTaken) BorderStroke(2.dp, borderColor) else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Top row with pill icon, name/dosage, and time/status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side with pill icon and medication info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pill icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = TimelyCareBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple pill representation using overlapping circles
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Pill body (rectangle with rounded ends)
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(8.dp)
                                    .background(
                                        color = TimelyCareBlue,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            // Pill cap (top half)
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(8.dp)
                                    .background(
                                        color = TimelyCareBlue.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .offset(x = (-5).dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Medicine name and dosage
                    Column {
                        Text(
                            text = medication.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TimelyCareTextPrimary
                        )
                        Text(
                            text = medication.dosage,
                            fontSize = 16.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        if (medication.specialInstructions.isNotBlank()) {
                            Text(
                                text = "Notes: ${medication.specialInstructions}",
                                fontSize = 14.sp,
                                color = TimelyCareTextPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Right side with time and status
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Time",
                            tint = TimelyCareTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = scheduledTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TimelyCareTextPrimary
                        )
                    }
                    Text(
                        text = if (isTaken) "Taken" else "Upcoming",
                        fontSize = 14.sp,
                        color = if (isTaken) Color(0xFF4CAF50) else TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom action button
            Button(
                onClick = {
                    if (isTaken) {
                        takenRepository.markAsNotTaken(medication.id, scheduledTime)
                    } else {
                        takenRepository.markAsTaken(medication.id, scheduledTime)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTaken) Color(0xFF4CAF50) else TimelyCareBlue
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                if (isTaken) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Taken",
                        tint = TimelyCareWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Taken",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Mark as Taken",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}