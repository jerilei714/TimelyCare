package com.example.wear

import com.google.android.gms.wearable.*

class WearDataListenerService : WearableListenerService() {

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        android.util.Log.d("WearDataListener", "onDataChanged called with ${dataEvents.count} events")

        dataEvents.forEach { event ->
            android.util.Log.d("WearDataListener", "Processing event type: ${event.type}")
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                android.util.Log.d("WearDataListener", "Data item path: ${item.uri.path}")
                if (item.uri.path?.compareTo(MEDICATION_PATH) == 0) {
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    val medicationsData = dataMap.getString(MEDICATION_KEY)
                    android.util.Log.d("WearDataListener", "Received medication data: $medicationsData")

                    val medications = parseMedications(medicationsData ?: "")
                    android.util.Log.d("WearDataListener", "Parsed ${medications.size} medications")

                    // Update local storage
                    updateWatchMedications(medications)
                } else {
                    android.util.Log.d("WearDataListener", "Path does not match: ${item.uri.path} != $MEDICATION_PATH")
                }
            }
        }
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

    private fun updateWatchMedications(medications: List<Medication>) {
        // Update repository using singleton
        val repository = MedicationRepository.getInstance(this)
        repository.updateMedications(medications)

        android.util.Log.d("WearDataListener", "Updated repository with ${medications.size} medications")

        // Log for debugging
        medications.forEach { med ->
            android.util.Log.d("WearMeds", "Received: ${med.name} - ${med.dosage}")
        }
    }
}