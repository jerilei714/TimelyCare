package com.example.wear

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val time: String,
    val frequency: String
)

class MedicationRepository private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("medications", Context.MODE_PRIVATE)

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    init {
        loadMedications()
    }

    fun updateMedications(medications: List<Medication>) {
        android.util.Log.d("WearMedicationRepo", "Updating medications: ${medications.size} items")
        _medications.value = medications
        saveMedications(medications)
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicationRepository? = null

        fun getInstance(context: Context): MedicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private fun saveMedications(medications: List<Medication>) {
        val medicationsString = medications.joinToString("|") { med ->
            "${med.id},${med.name},${med.dosage},${med.time},${med.frequency}"
        }
        prefs.edit().putString("medications_data", medicationsString).apply()
    }

    private fun loadMedications() {
        val medicationsString = prefs.getString("medications_data", "") ?: ""
        val medications = parseMedications(medicationsString)
        _medications.value = medications
    }

    private fun parseMedications(data: String): List<Medication> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { medString ->
            val parts = medString.split(",")
            if (parts.size == 5) {
                Medication(parts[0], parts[1], parts[2], parts[3], parts[4])
            } else null
        }
    }
}