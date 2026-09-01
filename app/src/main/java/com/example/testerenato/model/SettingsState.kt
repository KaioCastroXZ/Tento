package com.example.testerenato.model

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Preferências do usuário. Mantidas propositalmente enxutas. */
data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
)
