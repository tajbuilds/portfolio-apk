package com.taj.portfolio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF00695C),
    tertiary = androidx.compose.ui.graphics.Color(0xFF37474F),
    surface = androidx.compose.ui.graphics.Color(0xFFF6F8FA),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE8EEF5),
)

@Composable
fun PortfolioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
