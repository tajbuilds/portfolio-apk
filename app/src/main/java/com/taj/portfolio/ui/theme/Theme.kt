package com.taj.portfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B57D0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = Color(0xFF001A43),
    secondary = Color(0xFF1B6B70),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF6A4A8A),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE9EEF6),
    onSurfaceVariant = Color(0xFF4A5668),
    outline = Color(0xFFC6CFDC),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF001C4D),
    primaryContainer = Color(0xFF164B9E),
    onPrimaryContainer = Color(0xFFF0F5FF),
    secondary = Color(0xFF9AD9DD),
    onSecondary = Color(0xFF0B2C31),
    tertiary = Color(0xFFE1C4FF),
    onTertiary = Color(0xFF32124E),
    background = Color(0xFF070C14),
    onBackground = Color(0xFFF1F5FF),
    surface = Color(0xFF111B2A),
    onSurface = Color(0xFFF1F5FF),
    surfaceVariant = Color(0xFF1C2A3D),
    onSurfaceVariant = Color(0xFFD1DBEB),
    outline = Color(0xFF586A84),
    error = Color(0xFFFFB4AB),
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 27.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
)

@Composable
fun PortfolioTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
