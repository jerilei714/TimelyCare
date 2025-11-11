# TimelyCare - Medication Management App

TimelyCare is an Android medication management application designed specifically for older adults. The app features an intuitive interface with large touch targets, clear fonts, and accessibility considerations to help users manage their medication schedules effectively.

## Features

- **Medication Management**: Add, edit, and delete medications with detailed information
- **Daily Schedule**: View today's medication schedule with taken/upcoming status
- **Flexible Scheduling**: Set daily or custom day-specific medication frequencies
- **Accessibility Design**: Large buttons, clear fonts, and intuitive navigation for older adults
- **Date & Time Pickers**: Easy-to-use date and time selection optimized for accessibility
- **Wear OS Support**: Companion app for Wear OS devices
- **Material 3 Design**: Modern Android design system with custom theming

## Screenshots

The app includes a dashboard view and medication management interface designed for ease of use.

## Prerequisites

Before running this project, make sure you have the following installed:

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **Java Development Kit (JDK)**: Version 11 or newer
- **Android SDK**: API Level 34 (Android 14) or newer
- **Git**: For cloning the repository

### Hardware Requirements
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: At least 4GB free space
- **Android Device/Emulator**: API Level 24 (Android 7.0) or newer for testing

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/LuWes17/TimelyCare.git
cd TimelyCare
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned `TimelyCare` folder
4. Click "OK" to open the project

### 3. SDK Setup

When you first open the project, Android Studio may prompt you to:
- Install missing SDK platforms
- Download required build tools
- Accept license agreements

Click "Install" or "Accept" for all prompts.

### 4. Gradle Sync

Android Studio will automatically sync the project with Gradle files. If it doesn't start automatically:
1. Click "Sync Now" in the notification bar, or
2. Go to `File > Sync Project with Gradle Files`

Wait for the sync to complete (this may take a few minutes on the first run).

## Running the App

### Option 1: Using Android Emulator (Recommended for Testing)

1. **Create an Emulator**:
   - Click on "Device Manager" in Android Studio (usually on the right sidebar)
   - Click "Create Device"
   - Select "Phone" → "Pixel 6" (or any phone with API 30+)
   - Choose "API 34" (Android 14) as the system image
   - Click "Finish"

2. **Run the App**:
   - Click the green "Run" button (▶️) in the toolbar
   - Select your emulator from the device dropdown
   - Wait for the emulator to start and the app to install

### Option 2: Using Physical Android Device

1. **Enable Developer Options on your Android device**:
   - Go to `Settings > About Phone`
   - Tap "Build Number" 7 times until you see "You are now a developer"
   - Go back to `Settings > Developer Options`
   - Enable "USB Debugging"

2. **Connect and Run**:
   - Connect your device via USB
   - Allow USB debugging when prompted
   - Click the green "Run" button in Android Studio
   - Select your device from the list

## Project Structure

```
TimelyCare/
├── app/                          # Main Android app module
│   ├── src/main/java/com/example/timelycare/
│   │   ├── MainActivity.kt       # Main UI components and screens
│   │   ├── MedicationDataService.kt # Wear OS data service
│   │   ├── data/
│   │   │   ├── Medication.kt     # Data models
│   │   │   └── MedicationRepository.kt # Data management
│   │   └── ui/theme/            # App theming and colors
│   └── src/main/res/            # Resources (layouts, images, etc.)
├── wear/                        # Wear OS companion app
└── build.gradle.kts            # Project build configuration
```

## Key Components

### MainActivity.kt
Contains all the main UI components:
- `TimelyCareApp`: Main app navigation and state management
- `DashboardScreen`: Today's medication schedule
- `MedicationsScreen`: Medication list management
- `AddMedicineScreenContent`: Add/edit medication form
- `MedicationCard`: Individual medication display

### Data Layer
- `Medication`: Data class for medication information
- `MedicationRepository`: Manages medication data with StateFlow
- `MedicationType` & `Frequency`: Enums for medication properties

## Troubleshooting

### Common Issues

**1. Build Errors**
```bash
# Clean and rebuild the project
./gradlew clean
./gradlew build
```

**2. Gradle Sync Issues**
- Click `File > Invalidate Caches and Restart`
- Select "Invalidate and Restart"

**3. Emulator Won't Start**
- Ensure virtualization is enabled in your computer's BIOS
- Try creating a new emulator with a different API level
- Restart Android Studio

**4. App Crashes on Startup**
- Check the Logcat in Android Studio for error messages
- Ensure you're using API level 24 or higher

**5. Wear OS Module Issues**
- The wear module is optional for testing the main app
- Focus on the main `app` module for core functionality

### Getting Help

If you encounter issues:
1. Check the Android Studio "Build" and "Logcat" tabs for error messages
2. Make sure all SDK components are installed
3. Try running on a different emulator/device
4. Contact the team for assistance

## Development Notes

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM pattern with StateFlow
- **Design System**: Material 3

## Testing the App

### Core Features to Test
1. **Add Medication**: Tap the "+" button and fill out the form
2. **Edit Medication**: Tap the edit button on any medication card
3. **Delete Medication**: Tap the delete button on any medication card
4. **Mark as Taken**: Use the "Mark as Taken" button on the dashboard
5. **Frequency Selection**: Test daily and custom day selections
6. **Date/Time Pickers**: Verify the accessibility-focused pickers work properly

### Test Data
The app starts with no medications. Add a few test medications with different:
- Types (Pill, Tablet, Liquid, etc.)
- Frequencies (Daily, specific days)
- Times throughout the day
- Start/end dates

## Contributing

When making changes:
1. Create a new branch: `git checkout -b feature-name`
2. Make your changes
3. Test thoroughly on both emulator and device
4. Commit: `git commit -m "Description of changes"`
5. Push: `git push origin feature-name`
6. Create a pull request

## Team Members

Add your team member information here.

---

For questions or issues, please contact the development team or create an issue in the GitHub repository.