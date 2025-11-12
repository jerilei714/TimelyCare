package com.example.timelycare.service

import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await
import com.example.timelycare.data.Medication
import java.time.format.DateTimeFormatter

class MedicationDataService {

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
    }

    suspend fun sendMedicationsToWatch(
        dataClient: DataClient,
        medications: List<Medication>
    ) {
        val putDataReq = PutDataMapRequest.create(MEDICATION_PATH).apply {
            dataMap.putString(MEDICATION_KEY, medicationsToJson(medications))
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest()

        dataClient.putDataItem(putDataReq).await()
    }

    private fun medicationsToJson(medications: List<Medication>): String {
        // Convert to simplified format for watch communication
        return medications.joinToString("|") { med ->
            val firstTime = med.medicationTimes.firstOrNull()?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "No time"
            "${med.id},${med.name},${med.dosage},$firstTime,${med.frequency}"
        }
    }
}