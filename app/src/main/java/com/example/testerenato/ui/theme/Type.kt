package com.example.testerenato.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaults = Typography()

val TentoTypography = Typography(
    displayLarge = defaults.displayLarge.copy(
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.SansSerif,
    ),
    headlineMedium = defaults.headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = defaults.titleLarge.copy(fontWeight = FontWeight.Bold),
    labelLarge = defaults.labelLarge.copy(fontWeight = FontWeight.Bold),
)

/**
 * Estilo do número gigante do placar. Usa algarismos tabulares (`tnum`) para o
 * número não "pular" de largura ao ir de 1 para 11 — resolve o mesmo problema que
 * uma fonte de exibição resolveria, sem embutir fonte nova.
 */
val ScoreDigitStyle: TextStyle = TextStyle(
    fontWeight = FontWeight.Black,
    fontSize = 104.sp,
    lineHeight = 104.sp,
    fontFamily = FontFamily.SansSerif,
    fontFeatureSettings = "tnum",
)
