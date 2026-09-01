package com.example.testerenato.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.testerenato.model.ThemeMode

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = InkGreen,
    primaryContainer = GoldDark,
    onPrimaryContainer = OffWhite,
    secondary = TrucoRedBright,
    onSecondary = OffWhite,
    secondaryContainer = TrucoRedDark,
    onSecondaryContainer = OffWhite,
    // tertiary = "ficha dourada" viva, usada nos CTAs (Nova partida / Continuar / Começar).
    tertiary = Gold,
    onTertiary = InkGreen,
    background = FeltGreenDeep,
    onBackground = OffWhite,
    surface = FeltGreenDark,
    onSurface = OffWhite,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceVariant = FeltGreen,
    onSurfaceVariant = FeltGreenPale,
    outline = FeltGreenLight,
    outlineVariant = FeltGreen,
    error = DestructiveDark,
    onError = OffWhite,
)

private val LightColors = lightColorScheme(
    primary = GoldInkLight,
    onPrimary = OffWhite,
    primaryContainer = GoldSoftContainer,
    onPrimaryContainer = GoldSoftOnContainer,
    secondary = TrucoRed,
    onSecondary = OffWhite,
    secondaryContainer = RedSoftContainer,
    onSecondaryContainer = RedSoftOnContainer,
    tertiary = Gold,
    onTertiary = InkGreen,
    background = FeltGreenPaleBg,
    onBackground = InkGreen,
    surface = Ivory,
    onSurface = InkGreen,
    surfaceContainerHigh = IvoryHigh,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = FeltGreenDark,
    outline = FeltGreenLight,
    outlineVariant = SurfaceVariantLight,
    error = DestructiveLight,
    onError = OffWhite,
)

@Composable
fun TentoTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (dark) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TentoTypography,
        content = content,
    )
}
