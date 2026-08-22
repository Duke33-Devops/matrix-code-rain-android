package com.vibescage.matrixcoderain.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = CyberBlack,
    primaryContainer = MatrixGreenDim,
    onPrimaryContainer = PureWhite,
    secondary = CyberCyan,
    onSecondary = CyberBlack,
    background = CyberBlack,
    onBackground = PureWhite,
    surface = CyberDarkSurface,
    onSurface = PureWhite,
    surfaceVariant = CyberGlassBg,
    onSurfaceVariant = PureWhite
)

@Composable
fun MatrixCodeRainTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
