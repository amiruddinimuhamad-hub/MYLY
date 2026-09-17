package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RosePrimary,
    onPrimary = NightObsidian,
    primaryContainer = Color(0xFF5A2330),
    onPrimaryContainer = SoftLavender,
    secondary = PeachWarm,
    onSecondary = NightObsidian,
    secondaryContainer = NightSurfaceElevated,
    onSecondaryContainer = PeachLight,
    tertiary = MintCalm,
    background = NightObsidian,
    onBackground = NightTextPrimary,
    surface = NightSurface,
    onSurface = NightTextPrimary,
    surfaceVariant = NightSurfaceElevated,
    onSurfaceVariant = NightTextSecondary,
    outline = Color(0x33B5ADC2)
)

private val LightColorScheme = lightColorScheme(
    primary = RosePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PeachLight,
    onPrimaryContainer = Color(0xFF6B1826),
    secondary = RoseSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = BlushCream,
    onSecondaryContainer = Color(0xFF5E2E20),
    tertiary = MintCalm,
    background = BlushCream,
    onBackground = TextPrimaryLight,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = TextPrimaryLight,
    surfaceVariant = WarmSand,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0x1F2D2530)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MYLYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MyApplicationTheme(darkTheme = darkTheme, content = content)
}

