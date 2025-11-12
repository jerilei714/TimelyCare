package com.example.timelycare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DayOfWeek
import com.example.timelycare.ui.theme.*

@Composable
fun FrequencySelectionModal(
    currentFrequency: String,
    onDismiss: () -> Unit,
    onFrequencySelected: (String) -> Unit
) {
    fun parseFrequencyString(frequencyStr: String): Set<DayOfWeek> {
        return if (frequencyStr == "Daily") {
            setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        } else {
            val dayStrings = frequencyStr.split(", ")
            dayStrings.mapNotNull { dayStr ->
                when (dayStr.trim()) {
                    "Mon" -> DayOfWeek.MONDAY
                    "Tue" -> DayOfWeek.TUESDAY
                    "Wed" -> DayOfWeek.WEDNESDAY
                    "Thu" -> DayOfWeek.THURSDAY
                    "Fri" -> DayOfWeek.FRIDAY
                    "Sat" -> DayOfWeek.SATURDAY
                    "Sun" -> DayOfWeek.SUNDAY
                    else -> null
                }
            }.toSet()
        }
    }

    var selectedDays by remember { mutableStateOf(parseFrequencyString(currentFrequency)) }
    val isAllDaysSelected = selectedDays.size == 7

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TimelyCareWhite)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Frequency",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    onClick = {
                        selectedDays = if (isAllDaysSelected) {
                            emptySet()
                        } else {
                            setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAllDaysSelected) TimelyCareBlue.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    border = BorderStroke(2.dp, if (isAllDaysSelected) TimelyCareBlue else TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAllDaysSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = TimelyCareBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = "Daily",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY).forEach { dayOfWeek ->
                        val isSelected = selectedDays.contains(dayOfWeek)
                        Button(
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayOfWeek
                                } else {
                                    selectedDays + dayOfWeek
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TimelyCareBlue else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = if (!isSelected) BorderStroke(1.dp, TimelyCareGray) else null,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = dayOfWeek.toString(),
                                color = if (isSelected) TimelyCareWhite else TimelyCareGray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).forEach { dayOfWeek ->
                        val isSelected = selectedDays.contains(dayOfWeek)
                        Button(
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayOfWeek
                                } else {
                                    selectedDays + dayOfWeek
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TimelyCareBlue else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = if (!isSelected) BorderStroke(1.dp, TimelyCareGray) else null,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = dayOfWeek.toString(),
                                color = if (isSelected) TimelyCareWhite else TimelyCareGray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val frequencyString = if (isAllDaysSelected) {
                            "Daily"
                        } else {
                            selectedDays.joinToString(", ")
                        }
                        onFrequencySelected(frequencyString)
                        onDismiss()
                    },
                    enabled = selectedDays.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimelyCareBlue,
                        disabledContainerColor = TimelyCareGray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Done",
                        color = if (selectedDays.isNotEmpty()) TimelyCareWhite else TimelyCareGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}