package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val XmaDarkColorScheme = darkColorScheme(
    primary = XmaRedPrimary,
    onPrimary = Color.White,
    primaryContainer = XmaRedDark,
    onPrimaryContainer = Color.White,
    secondary = XmaGold,
    onSecondary = Color.Black,
    secondaryContainer = XmaWarningContainer,
    onSecondaryContainer = XmaGoldLight,
    tertiary = XmaSuccess,
    onTertiary = Color.White,
    background = XmaBackground,
    onBackground = XmaTextWhite,
    surface = XmaSurface,
    onSurface = XmaTextWhite,
    surfaceVariant = XmaSurfaceVariant,
    onSurfaceVariant = XmaTextMuted,
    outline = XmaOutline,
    error = XmaError,
    onError = Color.White,
    errorContainer = XmaErrorContainer,
    onErrorContainer = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce the strong, premium dark martial-arts/sports feel requested by the user
    MaterialTheme(
        colorScheme = XmaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
