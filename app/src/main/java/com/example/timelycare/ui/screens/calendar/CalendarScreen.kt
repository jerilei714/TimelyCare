package com.example.timelycare.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.MedicationRepository
import java.time.LocalDate

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val repository = remember { MedicationRepository.getInstance(context) }
    val medications by repository.medications.collectAsStateWithLifecycle()

    var selectedViewType by remember { mutableStateOf(CalendarViewType.WEEK) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Safely handle medications list with validation
    val safeMedications = medications.filter { medication ->
        // Filter out any malformed medications
        try {
            medication.name.isNotBlank() &&
            medication.dosage.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // View type dropdown
        CalendarViewDropdown(
            selectedView = selectedViewType,
            onViewSelected = { selectedViewType = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Calendar view based on selected type
        when (selectedViewType) {
            CalendarViewType.WEEK -> {
                WeekCalendarView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            CalendarViewType.TWO_WEEKS -> {
                TwoWeeksCalendarView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            CalendarViewType.MONTH -> {
                MonthCalendarView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }

        // Medications list for selected date
        MedicationListForDate(
            selectedDate = selectedDate,
            medications = safeMedications
        )
    }
}