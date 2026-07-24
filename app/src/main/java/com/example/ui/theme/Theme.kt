package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TaliGreen,
    onPrimary = PureWhite,
    primaryContainer = TaliGreenLight,
    onPrimaryContainer = TaliGreen,
    secondary = TaliNavy,
    onSecondary = PureWhite,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = TextPrimary,
    tertiary = TaliRed,
    onTertiary = PureWhite,
    tertiaryContainer = TaliRedLight,
    onTertiaryContainer = TaliRed,
    background = PureWhite,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = TaliGreen,
    onPrimary = PureWhite,
    primaryContainer = TaliGreenLight,
    onPrimaryContainer = TaliGreen,
    secondary = TaliNavy,
    onSecondary = PureWhite,
    tertiary = TaliRed,
    background = PureWhite, // Keep background light as requested
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary
)

@Composable
fun AmerKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PureWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
