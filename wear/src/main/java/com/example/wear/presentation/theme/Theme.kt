package com.example.wear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography
import com.example.wear.data.settings.AccentColor
import com.example.wear.data.settings.TextSize

val LocalTextSize = staticCompositionLocalOf { TextSize.MEDIUM }

private fun createColors(accentColor: AccentColor, isDarkMode: Boolean): Colors {
    val primaryColor = accentColor.color
    val primaryVariant = when (accentColor) {
        AccentColor.GREEN -> Color(0xFF00A843)
        AccentColor.BLUE -> Color(0xFF1976D2)
        AccentColor.PURPLE -> Color(0xFF7B1FA2)
        AccentColor.RED -> Color(0xFFD32F2F)
        AccentColor.ORANGE -> Color(0xFFF57C00)
        AccentColor.PINK -> Color(0xFFC2185B)
    }

    return if (isDarkMode) {
        Colors(
            primary = primaryColor,
            primaryVariant = primaryVariant,
            secondary = primaryColor,
            secondaryVariant = primaryVariant,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            error = Color(0xFFCF6679),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFB3B3B3),
            onError = Color.Black
        )
    } else {
        Colors(
            primary = primaryColor,
            primaryVariant = primaryVariant,
            secondary = primaryColor,
            secondaryVariant = primaryVariant,
            background = Color.White,
            surface = Color.White,
            error = Color(0xFFBA1A1A),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            onSurfaceVariant = Color(0xFF666666),
            onError = Color.White
        )
    }
}

private fun createTypography(textSize: TextSize): Typography {
    val scaleFactor = textSize.scaleFactor

    return Typography(
        display1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (40.sp.value * scaleFactor).sp
        ),
        title1 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (22.sp.value * scaleFactor).sp
        ),
        title2 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (20.sp.value * scaleFactor).sp
        ),
        title3 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (18.sp.value * scaleFactor).sp
        ),
        body1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (16.sp.value * scaleFactor).sp
        ),
        body2 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (14.sp.value * scaleFactor).sp
        ),
        caption1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (12.sp.value * scaleFactor).sp
        ),
        caption2 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (10.sp.value * scaleFactor).sp
        )
    )
}

@Composable
fun TimelyCareTheme(
    accentColor: AccentColor = AccentColor.GREEN,
    textSize: TextSize = TextSize.MEDIUM,
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = createColors(accentColor, isDarkMode)
    val typography = createTypography(textSize)

    CompositionLocalProvider(LocalTextSize provides textSize) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            content = content
        )
    }
}