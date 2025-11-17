package com.example.timelycare.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.Medication
import com.example.timelycare.data.Frequency
import com.example.timelycare.data.DayOfWeek
import com.example.timelycare.data.MedicationTakenRepository
import com.example.timelycare.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MedicationListForDate(
    selectedDate: LocalDate,
    medications: List<Medication>,
    modifier: Modifier = Modifier
) {
    val medicationTimesForDate = try {
        // Defensive null checks
        if (medications == null) {
            emptyList()
        } else {
            val scheduledMedications = medications.filterNotNull().filter { medication ->
                try {
                    isMedicationScheduledForDate(medication, selectedDate)
                } catch (e: Exception) {
                    // Skip this medication if filtering fails
                    false
                }
            }

            // Expand each medication to include all its times
            scheduledMedications.flatMap { medication ->
                medication.medicationTimes.map { time ->
                    Pair(medication, time)
                }
            }
        }
    } catch (e: Exception) {
        // Log error and return empty list to prevent crash
        emptyList()
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = try {
                "Medications for ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}"
            } catch (e: Exception) {
                "Medications for ${selectedDate.toString()}"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (medicationTimesForDate.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
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
                items(medicationTimesForDate) { (medication, time) ->
                    CalendarMedicationCard(
                        medication = medication,
                        scheduledTime = time,
                        date = selectedDate
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMedicationCard(
    medication: Medication,
    scheduledTime: LocalTime,
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val takenRepository = remember { MedicationTakenRepository.getInstance(context) }
    val takenRecords by takenRepository.takenRecords.collectAsStateWithLifecycle()

    val isTaken by remember(takenRecords, date, medication.id, scheduledTime) {
        derivedStateOf {
            takenRepository.isMedicationTaken(medication.id, scheduledTime, date)
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isTaken) Color(0xFF4CAF50).copy(alpha = 0.1f) else TimelyCareWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isTaken) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4CAF50)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Pill icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = TimelyCareBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Simple pill representation
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pill body (rectangle with rounded ends)
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(6.dp)
                                .background(
                                    color = TimelyCareBlue,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                        // Pill cap (top half)
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(6.dp)
                                .background(
                                    color = TimelyCareBlue.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .offset(x = (-4).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = medication.name.takeIf { it.isNotBlank() } ?: "Unknown Medicine",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = medication.dosage.takeIf { it.isNotBlank() } ?: "No dosage",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = scheduledTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Text(
                text = if (isTaken) "Taken" else "Upcoming",
                fontSize = 14.sp,
                color = if (isTaken) Color(0xFF4CAF50) else TimelyCareTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun isMedicationScheduledForDate(medication: Medication, date: LocalDate): Boolean {
    return try {
        // Defensive null checks
        if (medication == null) return false
        if (date == null) return false

        // Check if medication is within date range
        val startDate = medication.startDate
        val endDate = medication.endDate

        // With required dates, these should never be null, but check defensively
        if (startDate != null && date.isBefore(startDate)) return false
        if (endDate != null && date.isAfter(endDate)) return false

        // Check frequency with defensive null check
        val frequency = medication.frequency ?: return false
        return when (frequency) {
            is Frequency.Daily -> true
            is Frequency.SpecificDays -> {
                try {
                    val dayOfWeek = when (date.dayOfWeek) {
                        java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
                        java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
                        java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
                        java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
                        java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
                        java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
                        java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
                    }
                    frequency.days?.contains(dayOfWeek) ?: false
                } catch (e: Exception) {
                    false
                }
            }
        }
    } catch (e: Exception) {
        // Return false on any error to prevent crash
        false
    }
}