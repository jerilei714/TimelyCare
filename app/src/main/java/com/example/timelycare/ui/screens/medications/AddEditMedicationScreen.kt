package com.example.timelycare.ui.screens.medications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.timelycare.data.*
import com.example.timelycare.ui.theme.*
import com.example.timelycare.ui.components.FrequencySelectionModal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicationScreen(
    editingMedication: Medication? = null,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { MedicationRepository.getInstance() }

    // Helper functions to convert medication data back to form format
    fun formatDateToString(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: ""
    }

    fun formatTimeToString(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    fun formatFrequencyToString(freq: Frequency): String {
        return when (freq) {
            is Frequency.Daily -> "Daily"
            is Frequency.SpecificDays -> {
                if (freq.days.size == 7) "Daily"
                else freq.days.joinToString(", ")
            }
        }
    }

    // Form field states - initialize with editing medication data if available
    var medicineName by remember { mutableStateOf(editingMedication?.name ?: "") }
    var dosage by remember { mutableStateOf(editingMedication?.dosage ?: "") }
    var selectedType by remember { mutableStateOf(editingMedication?.type?.toString() ?: "Pill") }
    var frequency by remember { mutableStateOf(editingMedication?.let { formatFrequencyToString(it.frequency) } ?: "Daily") }
    var startDate by remember { mutableStateOf(formatDateToString(editingMedication?.startDate)) }
    var endDate by remember { mutableStateOf(formatDateToString(editingMedication?.endDate)) }
    var medicationTimes by remember {
        mutableStateOf(
            editingMedication?.medicationTimes?.map { formatTimeToString(it) }
                ?: listOf("08:00 AM", "01:00 PM", "08:00 PM")
        )
    }
    var specialInstructions by remember { mutableStateOf(editingMedication?.specialInstructions ?: "") }
    var showFrequencyModal by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingTimeIndex by remember { mutableIntStateOf(-1) }
    var isAddingNewTime by remember { mutableStateOf(false) }

    // Validation states
    var medicineNameError by remember { mutableStateOf("") }
    var dosageError by remember { mutableStateOf("") }
    var timesError by remember { mutableStateOf("") }

    fun validateForm(): Boolean {
        medicineNameError = if (medicineName.isBlank()) "Medicine name is required" else ""
        dosageError = if (dosage.isBlank()) "Dosage is required" else ""
        timesError = if (medicationTimes.isEmpty()) "At least one medication time is required" else ""

        return medicineNameError.isEmpty() && dosageError.isEmpty() && timesError.isEmpty()
    }

    fun parseTimeString(timeStr: String): LocalTime? {
        return try {
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
        } catch (e: DateTimeParseException) {
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e2: DateTimeParseException) {
                null
            }
        }
    }

    fun parseDateString(dateStr: String): LocalDate? {
        return try {
            if (dateStr.isBlank()) null
            else LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun parseFrequency(frequencyStr: String): Frequency {
        return if (frequencyStr == "Daily") {
            Frequency.Daily
        } else {
            val dayStrings = frequencyStr.split(", ")
            val days = dayStrings.mapNotNull { dayStr ->
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
            if (days.isEmpty()) Frequency.Daily else Frequency.SpecificDays(days)
        }
    }

    fun formatDisplayDate(dateStr: String): String {
        return if (dateStr.isBlank()) {
            "Select Date"
        } else {
            try {
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
            } catch (e: DateTimeParseException) {
                "Select Date"
            }
        }
    }

    fun formatTimeFromHourMinute(hour: Int, minute: Int): String {
        val localTime = LocalTime.of(hour, minute)
        return localTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    fun parseTimeToHourMinute(timeStr: String): Pair<Int, Int> {
        return try {
            val localTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
            Pair(localTime.hour, localTime.minute)
        } catch (e: DateTimeParseException) {
            Pair(9, 0) // Default to 9:00 AM
        }
    }

    fun sortMedicationTimes(times: List<String>): List<String> {
        return times.sortedBy { timeStr ->
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
            } catch (e: DateTimeParseException) {
                LocalTime.of(9, 0) // Default sort position
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = medicineName,
            onValueChange = {
                medicineName = it
                if (medicineNameError.isNotEmpty()) medicineNameError = ""
            },
            label = { Text("Medicine Name *") },
            placeholder = { Text("e.g., Amoxicillin") },
            isError = medicineNameError.isNotEmpty(),
            supportingText = if (medicineNameError.isNotEmpty()) {
                { Text(medicineNameError, color = Color.Red) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            )
        )

        OutlinedTextField(
            value = dosage,
            onValueChange = {
                dosage = it
                if (dosageError.isNotEmpty()) dosageError = ""
            },
            label = { Text("Dosage *") },
            placeholder = { Text("e.g., 500 mg") },
            isError = dosageError.isNotEmpty(),
            supportingText = if (dosageError.isNotEmpty()) {
                { Text(dosageError, color = Color.Red) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            )
        )

        Text(
            text = "Type *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = typeDropdownExpanded,
            onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    focusedLabelColor = TimelyCareBlue
                )
            )
            ExposedDropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }
            ) {
                listOf("Pill", "Tablet", "Capsule", "Liquid", "Injection", "Others").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Frequency *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { showFrequencyModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TimelyCareBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = frequency,
                color = TimelyCareWhite
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Start Date",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(startDate),
                            color = if (startDate.isBlank()) TimelyCareGray else TimelyCareTextPrimary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select start date",
                            tint = TimelyCareBlue
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "End Date",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(endDate),
                            color = if (endDate.isBlank()) TimelyCareGray else TimelyCareTextPrimary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select end date",
                            tint = TimelyCareBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Medication Times *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        medicationTimes.forEachIndexed { index, time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp
                )

                IconButton(
                    onClick = {
                        editingTimeIndex = index
                        isAddingNewTime = false
                        showTimePicker = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit time",
                        tint = TimelyCareBlue
                    )
                }

                Button(
                    onClick = {
                        medicationTimes = medicationTimes.filterIndexed { i, _ -> i != index }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Remove",
                        color = TimelyCareWhite,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Button(
            onClick = {
                editingTimeIndex = -1
                isAddingNewTime = true
                showTimePicker = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Add Time",
                color = TimelyCareWhite,
                fontSize = 16.sp
            )
        }

        Text(
            text = "Special Instructions",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = specialInstructions,
            onValueChange = { specialInstructions = it },
            placeholder = { Text("Enter any special instructions...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            ),
            maxLines = 4
        )

        if (timesError.isNotEmpty()) {
            Text(
                text = timesError,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (validateForm()) {
                    val parsedTimes = medicationTimes.mapNotNull { parseTimeString(it) }
                    val medication = if (editingMedication != null) {
                        // Update existing medication
                        editingMedication.copy(
                            name = medicineName.trim(),
                            dosage = dosage.trim(),
                            type = MedicationType.fromString(selectedType),
                            frequency = parseFrequency(frequency),
                            startDate = parseDateString(startDate),
                            endDate = parseDateString(endDate),
                            medicationTimes = parsedTimes,
                            specialInstructions = specialInstructions.trim()
                        )
                    } else {
                        // Create new medication
                        Medication(
                            name = medicineName.trim(),
                            dosage = dosage.trim(),
                            type = MedicationType.fromString(selectedType),
                            frequency = parseFrequency(frequency),
                            startDate = parseDateString(startDate),
                            endDate = parseDateString(endDate),
                            medicationTimes = parsedTimes,
                            specialInstructions = specialInstructions.trim()
                        )
                    }

                    if (editingMedication != null) {
                        repository.updateMedication(medication)
                    } else {
                        repository.addMedication(medication)
                    }
                    onSave()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TimelyCareBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (editingMedication != null) "Update Medicine" else "Add Medicine",
                color = TimelyCareWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showFrequencyModal) {
        FrequencySelectionModal(
            currentFrequency = frequency,
            onDismiss = { showFrequencyModal = false },
            onFrequencySelected = { frequency = it }
        )
    }

    // Start Date Picker
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                            startDate = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK", color = TimelyCareBlue, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel", color = TimelyCareGray, fontSize = 16.sp)
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState,
                title = {
                    Text(
                        text = "Select Start Date",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    // End Date Picker
    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                            endDate = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK", color = TimelyCareBlue, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel", color = TimelyCareGray, fontSize = 16.sp)
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState,
                title = {
                    Text(
                        text = "Select End Date",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            medicationTimes = medicationTimes,
            editingTimeIndex = editingTimeIndex,
            isAddingNewTime = isAddingNewTime,
            onTimeSelected = { newTimes ->
                medicationTimes = newTimes
            },
            onDismiss = {
                showTimePicker = false
                editingTimeIndex = -1
                isAddingNewTime = false
            },
            formatTimeFromHourMinute = ::formatTimeFromHourMinute,
            parseTimeToHourMinute = ::parseTimeToHourMinute,
            sortMedicationTimes = ::sortMedicationTimes
        )
    }
}