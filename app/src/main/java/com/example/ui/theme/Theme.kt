package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DedsecColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = CyberBlack,
    primaryContainer = NeonGreenMuted,
    onPrimaryContainer = NeonGreenBright,
    secondary = CyberCyan,
    onSecondary = CyberBlack,
    secondaryContainer = CyberCyanDim,
    onSecondaryContainer = CyberCyan,
    tertiary = DedsecPink,
    onTertiary = TextWhite,
    tertiaryContainer = DedsecPinkDim,
    onTertiaryContainer = DedsecPink,
    background = CyberBlack,
    onBackground = NeonGreen,
    surface = TerminalSurface,
    onSurface = NeonGreen,
    surfaceVariant = TerminalSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = TerminalBorder,
    error = AlertRed,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DedsecColorScheme,
        typography = CyberTypography,
        content = content
    )
}
