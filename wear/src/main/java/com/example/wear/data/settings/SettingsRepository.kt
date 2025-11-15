package com.example.wear.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context.dataStore).also { INSTANCE = it }
            }
        }

        private val TEXT_SIZE_KEY = stringPreferencesKey("text_size")
        private val ACCENT_COLOR_KEY = stringPreferencesKey("accent_color")
        private val WATCH_TYPE_KEY = stringPreferencesKey("watch_type")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val ENABLED_COMPLICATIONS_KEY = stringSetPreferencesKey("enabled_complications")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            // If there's an error reading data, emit default settings
            emit(emptyPreferences())
        }.map { preferences ->
            AppSettings(
                textSize = TextSize.valueOf(
                    preferences[TEXT_SIZE_KEY] ?: TextSize.MEDIUM.name
                ),
                accentColor = AccentColor.valueOf(
                    preferences[ACCENT_COLOR_KEY] ?: AccentColor.GREEN.name
                ),
                watchType = WatchType.valueOf(
                    preferences[WATCH_TYPE_KEY] ?: WatchType.DIGITAL.name
                ),
                isDarkMode = preferences[DARK_MODE_KEY] ?: false,
                enabledComplications = preferences[ENABLED_COMPLICATIONS_KEY]
                    ?.mapNotNull { featureName ->
                        try {
                            ComplicationFeature.valueOf(featureName)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }?.toSet() ?: ComplicationFeature.values().toSet()
            )
        }

    suspend fun updateTextSize(textSize: TextSize) {
        dataStore.edit { preferences ->
            preferences[TEXT_SIZE_KEY] = textSize.name
        }
    }

    suspend fun updateAccentColor(accentColor: AccentColor) {
        dataStore.edit { preferences ->
            preferences[ACCENT_COLOR_KEY] = accentColor.name
        }
    }

    suspend fun updateWatchType(watchType: WatchType) {
        dataStore.edit { preferences ->
            preferences[WATCH_TYPE_KEY] = watchType.name
        }
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    suspend fun updateEnabledComplications(complications: Set<ComplicationFeature>) {
        dataStore.edit { preferences ->
            // Always ensure Settings and All Meds are enabled
            val finalComplications = complications + ComplicationFeature.SETTINGS + ComplicationFeature.ALL_MEDS
            preferences[ENABLED_COMPLICATIONS_KEY] = finalComplications.map { it.name }.toSet()
        }
    }

    suspend fun toggleComplication(complication: ComplicationFeature, enabled: Boolean) {
        // Don't allow disabling always-enabled features
        if (complication.isAlwaysEnabled) return

        dataStore.edit { preferences ->
            val currentComplications = preferences[ENABLED_COMPLICATIONS_KEY]
                ?.mapNotNull { featureName ->
                    try {
                        ComplicationFeature.valueOf(featureName)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }?.toMutableSet() ?: ComplicationFeature.values().toMutableSet()

            if (enabled) {
                currentComplications.add(complication)
            } else {
                currentComplications.remove(complication)
            }

            // Always ensure Settings and All Meds are enabled
            currentComplications.add(ComplicationFeature.SETTINGS)
            currentComplications.add(ComplicationFeature.ALL_MEDS)

            preferences[ENABLED_COMPLICATIONS_KEY] = currentComplications.map { it.name }.toSet()
        }
    }
}