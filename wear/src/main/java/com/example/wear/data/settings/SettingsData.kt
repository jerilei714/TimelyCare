package com.example.wear.data.settings

import androidx.compose.ui.graphics.Color

enum class TextSize(val displayName: String, val scaleFactor: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f)
}

enum class AccentColor(val displayName: String, val color: Color) {
    GREEN("Green", Color(0xFF00C853)),
    BLUE("Blue", Color(0xFF2196F3)),
    PURPLE("Purple", Color(0xFF9C27B0)),
    RED("Red", Color(0xFFF44336)),
    ORANGE("Orange", Color(0xFFFF9800)),
    PINK("Pink", Color(0xFFE91E63))
}

enum class WatchType(val displayName: String) {
    DIGITAL("Digital"),
    ANALOG("Analog")
}

enum class ComplicationFeature(val displayName: String, val isAlwaysEnabled: Boolean = false) {
    SETTINGS("Settings", true),
    ALL_MEDS("All Meds", true),
    HISTORY("History"),
    MAINTENANCE("Maintenance"),
    EMERGENCY("Emergency"),
    VITALS("Vitals"),
    UPCOMING("Upcoming")
}

data class AppSettings(
    val textSize: TextSize = TextSize.MEDIUM,
    val accentColor: AccentColor = AccentColor.GREEN,
    val watchType: WatchType = WatchType.DIGITAL,
    val isDarkMode: Boolean = false,
    val enabledComplications: Set<ComplicationFeature> = ComplicationFeature.values().toSet()
)