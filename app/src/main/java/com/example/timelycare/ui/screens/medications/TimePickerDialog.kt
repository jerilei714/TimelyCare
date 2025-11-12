package com.example.timelycare.ui.screens.medications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.ui.theme.*

@Composable
fun TimePickerDialog(
    medicationTimes: List<String>,
    editingTimeIndex: Int,
    isAddingNewTime: Boolean,
    onTimeSelected: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    formatTimeFromHourMinute: (Int, Int) -> String,
    parseTimeToHourMinute: (String) -> Pair<Int, Int>,
    sortMedicationTimes: (List<String>) -> List<String>
) {
    val currentTime = if (editingTimeIndex >= 0 && editingTimeIndex < medicationTimes.size) {
        parseTimeToHourMinute(medicationTimes[editingTimeIndex])
    } else {
        Pair(9, 0) // Default to 9:00 AM for new times
    }

    var hourText by remember { mutableStateOf(if (currentTime.first > 12) (currentTime.first - 12).toString() else if (currentTime.first == 0) "12" else currentTime.first.toString()) }
    var minuteText by remember { mutableStateOf(String.format("%02d", currentTime.second)) }
    var isAM by remember { mutableStateOf(currentTime.first < 12) }

    var hourError by remember { mutableStateOf("") }
    var minuteError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isAddingNewTime) "Add Medication Time" else "Edit Medication Time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Enter Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Hour and Minute Input Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Hour Field
                    Column {
                        Text(
                            text = "Hour",
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    hourText = newValue
                                    hourError = when {
                                        newValue.isEmpty() -> "Required"
                                        newValue.toIntOrNull() == null -> "Invalid"
                                        newValue.toInt() < 1 || newValue.toInt() > 12 -> "1-12 only"
                                        else -> ""
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = hourError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TimelyCareBlue,
                                focusedLabelColor = TimelyCareBlue
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    // Colon Separator
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    // Minute Field
                    Column {
                        Text(
                            text = "Minute",
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    minuteText = newValue
                                    minuteError = when {
                                        newValue.isEmpty() -> "Required"
                                        newValue.toIntOrNull() == null -> "Invalid"
                                        newValue.toInt() < 0 || newValue.toInt() > 59 -> "0-59 only"
                                        else -> ""
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = minuteError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TimelyCareBlue,
                                focusedLabelColor = TimelyCareBlue
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                // Error Messages
                if (hourError.isNotEmpty() || minuteError.isNotEmpty()) {
                    Text(
                        text = when {
                            hourError.isNotEmpty() -> "Hour: $hourError"
                            minuteError.isNotEmpty() -> "Minute: $minuteError"
                            else -> ""
                        },
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // AM/PM Radio Buttons
                Text(
                    text = "Time of Day",
                    fontSize = 14.sp,
                    color = TimelyCareTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // AM Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = isAM,
                                onClick = { isAM = true }
                            )
                            .padding(4.dp)
                    ) {
                        RadioButton(
                            selected = isAM,
                            onClick = { isAM = true },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TimelyCareBlue,
                                unselectedColor = TimelyCareGray
                            )
                        )
                        Text(
                            text = "AM",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // PM Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = !isAM,
                                onClick = { isAM = false }
                            )
                            .padding(4.dp)
                    ) {
                        RadioButton(
                            selected = !isAM,
                            onClick = { isAM = false },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TimelyCareBlue,
                                unselectedColor = TimelyCareGray
                            )
                        )
                        Text(
                            text = "PM",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate inputs
                    val hour = hourText.toIntOrNull()
                    val minute = minuteText.toIntOrNull()

                    if (hour != null && minute != null &&
                        hour in 1..12 && minute in 0..59 &&
                        hourError.isEmpty() && minuteError.isEmpty()) {

                        // Convert to 24-hour format
                        val hour24 = when {
                            hour == 12 && isAM -> 0
                            hour == 12 && !isAM -> 12
                            isAM -> hour
                            else -> hour + 12
                        }

                        val newTimeStr = formatTimeFromHourMinute(hour24, minute)

                        if (isAddingNewTime) {
                            // Check for duplicates
                            if (!medicationTimes.contains(newTimeStr)) {
                                onTimeSelected(sortMedicationTimes(medicationTimes + newTimeStr))
                            }
                        } else if (editingTimeIndex >= 0 && editingTimeIndex < medicationTimes.size) {
                            // Update existing time
                            val updatedTimes = medicationTimes.toMutableList()
                            updatedTimes[editingTimeIndex] = newTimeStr
                            onTimeSelected(sortMedicationTimes(updatedTimes))
                        }

                        onDismiss()
                    }
                }
            ) {
                Text(
                    "OK",
                    color = TimelyCareBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Cancel",
                    color = TimelyCareGray,
                    fontSize = 16.sp
                )
            }
        }
    )
}