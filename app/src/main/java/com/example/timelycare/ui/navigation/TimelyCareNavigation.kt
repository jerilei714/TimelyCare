package com.example.timelycare.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.timelycare.ui.theme.TimelyCareBackground
import com.example.timelycare.ui.components.TimelyCareBottomNavigation
import com.example.timelycare.ui.components.TimelyCareTopBar
import com.example.timelycare.ui.components.AddMedicineHeader
import com.example.timelycare.ui.components.MedicationsHeader
import com.example.timelycare.ui.screens.dashboard.DashboardScreen
import com.example.timelycare.ui.screens.medications.MedicationsScreen
import com.example.timelycare.ui.screens.medications.AddEditMedicationScreen
import com.example.timelycare.ui.screens.calendar.CalendarScreen
import com.example.timelycare.ui.screens.contacts.ContactsScreen
import com.example.timelycare.data.Medication

@Composable
fun TimelyCareApp() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddMedicine by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<Medication?>(null) }

    Scaffold(
        topBar = {
            when {
                showAddMedicine -> AddMedicineHeader(
                    isEditing = editingMedication != null,
                    onBackClick = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                selectedTabIndex == 1 -> MedicationsHeader(onAddClick = { showAddMedicine = true })
                else -> TimelyCareTopBar()
            }
        },
        bottomBar = {
            if (!showAddMedicine) {
                TimelyCareBottomNavigation(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }
        },
        containerColor = TimelyCareBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                showAddMedicine -> AddEditMedicationScreen(
                    editingMedication = editingMedication,
                    onSave = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                selectedTabIndex == 0 -> DashboardScreen()
                selectedTabIndex == 1 -> MedicationsScreen(
                    onAddClick = { showAddMedicine = true },
                    onEditClick = { medication ->
                        editingMedication = medication
                        showAddMedicine = true
                    }
                )
                selectedTabIndex == 2 -> CalendarScreen()
                selectedTabIndex == 3 -> ContactsScreen()
            }
        }
    }
}