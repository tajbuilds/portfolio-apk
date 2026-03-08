package com.taj.portfolio.ui.theme

import android.content.Context

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getThemeMode(): ThemeMode {
        val stored = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.SYSTEM
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    private companion object {
        const val PREFS_NAME = "portfolio_prefs"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
