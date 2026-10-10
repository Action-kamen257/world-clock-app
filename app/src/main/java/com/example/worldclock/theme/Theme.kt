package com.example.worldclock.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WorldClockDarkScheme = darkColorScheme(
    primary = Amber,
    onPrimary = DarkBackground,
    primaryContainer = AmberDark,
    onPrimaryContainer = AmberLight,
    secondary = Teal,
    onSecondary = DarkBackground,
    secondaryContainer = TealDark,
    onSecondaryContainer = TealLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
)

@Composable
fun WorldClockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Always use dark theme for this app — it's a clock app, dark is premium
    MaterialTheme(
        colorScheme = WorldClockDarkScheme,
        typography = Typography,
        content = content,
    )
}
