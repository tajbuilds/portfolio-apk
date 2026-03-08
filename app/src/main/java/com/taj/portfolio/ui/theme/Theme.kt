package com.taj.portfolio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF00695C),
    tertiary = androidx.compose.ui.graphics.Color(0xFF37474F),
    surface = androidx.compose.ui.graphics.Color(0xFFF6F8FA),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE8EEF5),
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8CB4FF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF002A60),
    secondary = androidx.compose.ui.graphics.Color(0xFF80CBC4),
    tertiary = androidx.compose.ui.graphics.Color(0xFFB0BEC5),
    surface = androidx.compose.ui.graphics.Color(0xFF0F1722),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF1E2936),
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
        content = content,
    )
}
