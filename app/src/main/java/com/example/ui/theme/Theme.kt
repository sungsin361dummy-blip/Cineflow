package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CinematicColorScheme = darkColorScheme(
    primary = AmberGoldPrimary,
    onPrimary = AmberGoldOnPrimary,
    primaryContainer = AmberGoldContainer,
    onPrimaryContainer = AmberGoldOnContainer,
    secondary = AnamorphicCyan,
    onSecondary = CinemaBackground,
    secondaryContainer = AnamorphicCyanContainer,
    onSecondaryContainer = AnamorphicCyanOnContainer,
    tertiary = ContinuityEmerald,
    onTertiary = CinemaBackground,
    tertiaryContainer = ContinuityEmeraldContainer,
    onTertiaryContainer = ContinuityEmeraldOnContainer,
    background = CinemaBackground,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CinemaBorder,
    outlineVariant = CinemaDivider
)

@Composable
fun CineFlowTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CinemaBackground.toArgb()
            window.navigationBarColor = CinemaBackground.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = CinematicColorScheme,
        typography = Typography,
        content = content
    )
}
