package com.example.wear.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wear.data.settings.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateTextSize(textSize: TextSize) {
        viewModelScope.launch {
            settingsRepository.updateTextSize(textSize)
        }
    }

    fun updateAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            settingsRepository.updateAccentColor(accentColor)
        }
    }

    fun updateWatchType(watchType: WatchType) {
        viewModelScope.launch {
            settingsRepository.updateWatchType(watchType)
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDarkMode(isDarkMode)
        }
    }

    fun toggleComplication(complication: ComplicationFeature, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleComplication(complication, enabled)
        }
    }

    fun getEnabledComplications(): List<ComplicationFeature> {
        return settings.value.enabledComplications.sortedBy {
            // Settings always first, All Meds second, then others in order
            when (it) {
                ComplicationFeature.SETTINGS -> 0
                ComplicationFeature.ALL_MEDS -> 1
                ComplicationFeature.HISTORY -> 2
                ComplicationFeature.MAINTENANCE -> 3
                ComplicationFeature.EMERGENCY -> 4
                ComplicationFeature.VITALS -> 5
                ComplicationFeature.UPCOMING -> 6
            }
        }
    }
}