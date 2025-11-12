# TimelyCare System Architecture

## Overview

TimelyCare is a medication management application built using modern Android development practices with Jetpack Compose, following MVVM architecture pattern and designed for accessibility for older adults.

## High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Mobile App    │◄──►│  Wear OS App    │◄──►│  Data Layer     │
│  (Primary UI)   │    │ (Companion UI)  │    │   (Local)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Detailed System Architecture

### 1. Presentation Layer (UI Layer)

#### **Mobile App Module (`app/`)**
```
MainActivity.kt
├── TimelyCareApp (Main Composable)
├── DashboardScreen
├── MedicationsScreen
├── AddMedicineScreenContent
└── UI Components
    ├── MedicationCard
    ├── TodayMedicationCard
    ├── FrequencySelectionModal
    └── Custom Date/Time Pickers
```

**Key Components:**
- **MainActivity.kt**: Single activity containing all UI screens
- **Jetpack Compose**: Declarative UI framework
- **Material 3 Design**: Modern Android design system
- **State Management**: Compose state with `remember` and `mutableStateOf`
- **Navigation**: Tab-based navigation with bottom navigation bar

#### **Wear OS Module (`wear/`)**
```
wear/src/main/java/com/example/wear/
├── presentation/
│   └── MainActivity.kt (Watch UI)
├── complication/
│   └── MainComplicationService.kt
├── tile/
│   └── MainTileService.kt
└── WearDataListenerService.kt
```

### 2. Domain Layer

#### **Data Models (`app/src/main/java/com/example/timelycare/data/`)**
```kotlin
// Core Data Models
data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val type: MedicationType,
    val frequency: Frequency,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val medicationTimes: List<LocalTime>,
    val specialInstructions: String
)

enum class MedicationType {
    PILL, TABLET, CAPSULE, LIQUID, INJECTION, OTHERS
}

sealed class Frequency {
    object Daily : Frequency()
    data class SpecificDays(val days: Set<DayOfWeek>) : Frequency()
}
```

### 3. Data Layer

#### **Repository Pattern (`MedicationRepository.kt`)**
```kotlin
class MedicationRepository {
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    // CRUD Operations
    fun addMedication(medication: Medication)
    fun updateMedication(medication: Medication)
    fun deleteMedication(medicationId: String)
    fun getMedicationById(id: String): Medication?
}
```

**Data Storage:**
- **Local Storage**: In-memory using `StateFlow`
- **No Database**: Data resets on app restart (suitable for research study)
- **Thread Safety**: Repository uses thread-safe StateFlow

### 4. Communication Layer

#### **Wear OS Data Sync (`MedicationDataService.kt`)**
```kotlin
class MedicationDataService {
    suspend fun sendMedicationsToWatch(
        dataClient: DataClient,
        medications: List<Medication>
    )

    private fun medicationsToJson(medications: List<Medication>): String
}
```

**Sync Architecture:**
```
Mobile App Repository
        ↓
MedicationDataService
        ↓
Wear OS Data Layer API
        ↓ (Bluetooth/Wi-Fi/Cloud)
Watch Data Listener Service
        ↓
Wear OS UI Update
```

## Component Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        MOBILE APP MODULE                        │
├─────────────────────────────────────────────────────────────────┤
│  UI LAYER (Jetpack Compose)                                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  DashboardScreen│  │MedicationsScreen│  │ AddMedicineForm │ │
│  │                 │  │                 │  │                 │ │
│  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │ │
│  │ │TodayMedCard │ │  │ │MedicationCard│ │  │ │Form Fields  │ │ │
│  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  DOMAIN LAYER                                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              MedicationRepository                           │ │
│  │                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐ │ │
│  │  │    Add      │    │   Update    │    │     Delete      │ │ │
│  │  │ Medication  │    │ Medication  │    │   Medication    │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  DATA LAYER                                                     │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  StateFlow<List<Medication>>  (In-Memory Storage)          │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                    ↓ (Data Sync)
┌─────────────────────────────────────────────────────────────────┐
│                    WEAR OS DATA LAYER                          │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              MedicationDataService                          │ │
│  │  sendMedicationsToWatch() → DataClient.putDataItem()       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                    ↓ (Bluetooth/WiFi/Cloud)
┌─────────────────────────────────────────────────────────────────┐
│                       WEAR OS MODULE                           │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │               WearDataListenerService                       │ │
│  │                        ↓                                   │ │
│  │               Watch UI Components                          │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │ │
│  │  │   Watch Face    │  │ Complications   │  │    Tiles    │ │ │
│  │  │   Integration   │  │   (Quick View)  │  │(Home Screen)│ │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow Architecture

### 1. **Add Medication Flow**
```
User Input (AddMedicineForm)
        ↓
Form Validation
        ↓
Create Medication Object
        ↓
MedicationRepository.addMedication()
        ↓
Update StateFlow<List<Medication>>
        ↓
UI Recomposition (Automatic)
        ↓
MedicationDataService.sendMedicationsToWatch()
        ↓
Wear OS Data Layer Sync
```

### 2. **Edit Medication Flow**
```
User Taps Edit Button
        ↓
Pass Medication Object to Edit Form
        ↓
Pre-populate Form Fields
        ↓
User Modifies Data
        ↓
Form Validation
        ↓
MedicationRepository.updateMedication()
        ↓
Update StateFlow (Replace existing)
        ↓
UI Recomposition
        ↓
Sync to Watch
```

### 3. **Dashboard Display Flow**
```
Repository.medications StateFlow
        ↓
collectAsStateWithLifecycle()
        ↓
Filter for Today's Schedule
        ↓
TodayMedicationCard Components
        ↓
Display with Status (Taken/Upcoming)
```

## Technology Stack

### **Mobile App**
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Repository Pattern
- **State Management**: StateFlow + Compose State
- **Design System**: Material 3
- **Navigation**: Compose Navigation (Tab-based)

### **Wear OS**
- **Platform**: Wear OS 3.0+
- **Sync**: Wear OS Data Layer API
- **UI**: Wear Compose
- **Services**: Complications, Tiles, Data Listener

### **Data & Storage**
- **Local Storage**: In-memory StateFlow
- **Data Format**: Kotlin Data Classes
- **Serialization**: Custom JSON for Wear OS sync

### **Communication**
- **Phone ↔ Watch**: Wear OS Data Layer
- **Protocols**: Bluetooth, Wi-Fi, Cloud Sync
- **API**: Google Wear OS DataClient

## Security & Privacy

### **Data Protection**
- **Local Storage Only**: No cloud storage of medication data
- **Device Isolation**: Each installation is sandboxed
- **No Network Calls**: No external API dependencies
- **Wear OS Encryption**: Automatic encryption via Wear OS Data Layer

### **Privacy Features**
- **No User Authentication**: No login required
- **No Data Collection**: No analytics or tracking
- **Offline Operation**: Works without internet
- **Data Locality**: Data never leaves user's devices

## Scalability & Performance

### **Performance Optimizations**
- **Lazy Loading**: LazyColumn for medication lists
- **State Optimization**: Efficient recomposition with Compose
- **Memory Management**: Lightweight in-memory storage
- **Wear OS**: Efficient data serialization for sync

### **Accessibility Features**
- **Large Touch Targets**: 48dp minimum for older adults
- **High Contrast**: Material 3 theming
- **Clear Typography**: Optimized font sizes
- **Simple Navigation**: Tab-based, minimal complexity

## Deployment Architecture

### **Build Configuration**
```
app/
├── build.gradle.kts (Android app configuration)
├── proguard-rules.pro (Code obfuscation)
└── src/
    ├── main/ (Production code)
    ├── test/ (Unit tests)
    └── androidTest/ (UI tests)

wear/
├── build.gradle.kts (Wear OS configuration)
└── src/main/ (Wear OS code)
```

### **Distribution Model**
- **APK Distribution**: Direct APK sharing for research
- **Independent Installations**: Each participant gets isolated app
- **No Backend Required**: Fully client-side application

## Future Extensibility

### **Potential Enhancements**
- **Database Integration**: Room database for persistence
- **Cloud Sync**: Firebase or other cloud backends
- **Notifications**: Medication reminders
- **Analytics**: Usage tracking for research
- **Multi-user**: Family medication management

### **Architecture Support**
- **Modular Design**: Easy to add new features
- **Clean Separation**: UI, Domain, Data layers are isolated
- **Dependency Injection**: Ready for Hilt/Dagger integration
- **Testing**: Architecture supports unit and integration testing

This architecture provides a solid foundation for a medication management app while maintaining simplicity and accessibility for older adult users.