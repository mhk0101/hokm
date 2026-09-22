package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val HokmColorScheme = darkColorScheme(
    primary = PersianGold,
    onPrimary = VelvetNight,
    primaryContainer = PersianCrimson,
    onPrimaryContainer = Color.White,
    secondary = PersianTurquoise,
    onSecondary = VelvetNight,
    tertiary = PersianGoldDark,
    background = VelvetNight,
    onBackground = HokmTextPrimary,
    surface = VelvetSurface,
    onSurface = HokmTextPrimary,
    surfaceVariant = VelvetSurfaceVariant,
    onSurfaceVariant = HokmTextSecondary
)

@Composable
fun HokmChiTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = HokmColorScheme,
            typography = Typography,
            content = content
        )
    }
}

