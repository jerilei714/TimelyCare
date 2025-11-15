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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val repository = remember { MedicationRepository.getInstance(context) }

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
    var startDate by remember {
        mutableStateOf(
            if (editingMedication != null) {
                formatDateToString(editingMedication.startDate)
            } else {
                // Set default start date to today for new medications
                LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            }
        )
    }
    var endDate by remember {
        mutableStateOf(
            if (editingMedication != null) {
                formatDateToString(editingMedication.endDate)
            } else {
                // Set default end date to 30 days from now for new medications
                LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            }
        )
    }
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
    var startDateError by remember { mutableStateOf("") }
    var endDateError by remember { mutableStateOf("") }

    // Helper function for parsing dates (declared before validation)
    fun parseDateString(dateStr: String): LocalDate? {
        return try {
            if (dateStr.isBlank()) null
            else LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun validateForm(): Boolean {
        medicineNameError = if (medicineName.isBlank()) "Medicine name is required" else ""
        dosageError = if (dosage.isBlank()) "Dosage is required" else ""
        timesError = if (medicationTimes.isEmpty()) "At least one medication time is required" else ""

        // Validate start date
        startDateError = when {
            startDate.isBlank() -> "Start date is required"
            parseDateString(startDate) == null -> "Invalid start date format"
            else -> ""
        }

        // Validate end date
        endDateError = when {
            endDate.isBlank() -> "End date is required"
            parseDateString(endDate) == null -> "Invalid end date format"
            else -> ""
        }

        // Validate date range (end date should be after or equal to start date)
        if (startDateError.isEmpty() && endDateError.isEmpty()) {
            val parsedStartDate = parseDateString(startDate)
            val parsedEndDate = parseDateString(endDate)
            if (parsedStartDate != null && parsedEndDate != null) {
                if (parsedEndDate.isBefore(parsedStartDate)) {
                    endDateError = "End date must be after start date"
                }
            }
        }

        return medicineNameError.isEmpty() && dosageError.isEmpty() && timesError.isEmpty() &&
               startDateError.isEmpty() && endDateError.isEmpty()
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

    fun parseFrequency(frequencyStr: String): Frequency {
        return try {
            if (frequencyStr == "Daily") {
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
        } catch (e: Exception) {
            // Default to Daily frequency if parsing fails
            Frequency.Daily
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
                    text = "Start Date *",
                    fontWeight = FontWeight.Medium,
                    color = if (startDateError.isNotEmpty()) Color.Red else TimelyCareTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        showStartDatePicker = true
                        if (startDateError.isNotEmpty()) startDateError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (startDateError.isNotEmpty()) Color.Red else TimelyCareGray
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(startDate),
                            color = when {
                                startDateError.isNotEmpty() -> Color.Red
                                startDate.isBlank() -> TimelyCareGray
                                else -> TimelyCareTextPrimary
                            },
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select start date",
                            tint = if (startDateError.isNotEmpty()) Color.Red else TimelyCareBlue
                        )
                    }
                }
                if (startDateError.isNotEmpty()) {
                    Text(
                        text = startDateError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "End Date *",
                    fontWeight = FontWeight.Medium,
                    color = if (endDateError.isNotEmpty()) Color.Red else TimelyCareTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        showEndDatePicker = true
                        if (endDateError.isNotEmpty()) endDateError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (endDateError.isNotEmpty()) Color.Red else TimelyCareGray
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(endDate),
                            color = when {
                                endDateError.isNotEmpty() -> Color.Red
                                endDate.isBlank() -> TimelyCareGray
                                else -> TimelyCareTextPrimary
                            },
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select end date",
                            tint = if (endDateError.isNotEmpty()) Color.Red else TimelyCareBlue
                        )
                    }
                }
                if (endDateError.isNotEmpty()) {
                    Text(
                        text = endDateError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                    try {
                        val parsedTimes = medicationTimes.mapNotNull { parseTimeString(it) }.ifEmpty {
                            // Ensure at least one default time if parsing fails
                            listOf(LocalTime.of(9, 0))
                        }
                        // Ensure safe string values
                        val safeName = medicineName.trim().takeIf { it.isNotBlank() } ?: "Medicine"
                        val safeDosage = dosage.trim().takeIf { it.isNotBlank() } ?: "1 dose"
                        val safeInstructions = specialInstructions.trim()

                        // Parse and validate dates
                        val parsedStartDate = parseDateString(startDate)
                        val parsedEndDate = parseDateString(endDate)

                        val medication = if (editingMedication != null) {
                            // Update existing medication
                            editingMedication.copy(
                                name = safeName,
                                dosage = safeDosage,
                                type = MedicationType.fromString(selectedType),
                                frequency = parseFrequency(frequency),
                                startDate = parsedStartDate,
                                endDate = parsedEndDate,
                                medicationTimes = parsedTimes,
                                specialInstructions = safeInstructions
                            )
                        } else {
                            // Create new medication
                            Medication(
                                name = safeName,
                                dosage = safeDosage,
                                type = MedicationType.fromString(selectedType),
                                frequency = parseFrequency(frequency),
                                startDate = parsedStartDate,
                                endDate = parsedEndDate,
                                medicationTimes = parsedTimes,
                                specialInstructions = safeInstructions
                            )
                        }

                        if (editingMedication != null) {
                            repository.updateMedication(medication)
                        } else {
                            repository.addMedication(medication)
                        }
                        onSave()
                    } catch (e: Exception) {
                        // Handle any error during medication creation/saving
                        // For now, just prevent crash - could add error message later
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
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