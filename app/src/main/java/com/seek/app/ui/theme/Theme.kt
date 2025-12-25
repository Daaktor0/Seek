package com.seek.app.ui.theme

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
    primary = SageGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = SageGreenDark,
    
    secondary = SageGreenLight,
    onSecondary = SoftBlack,
    secondaryContainer = SoftCream,
    onSecondaryContainer = SageGreenDark,
    
    tertiary = SoftGold,
    onTertiary = SoftBlack,
    tertiaryContainer = SoftGoldLight,
    onTertiaryContainer = DarkGray,
    
    background = WarmOffWhite,
    onBackground = SoftBlack,
    
    surface = SurfaceWhite,
    onSurface = SoftBlack,
    surfaceVariant = SoftCream,
    onSurfaceVariant = DarkGray,
    
    outline = LightGray,
    outlineVariant = UltraLightGray,
    
    // No error/red colors - use soft gold instead for "attention needed"
    error = SoftGold,
    onError = SoftBlack,
    errorContainer = SoftGoldLight,
    onErrorContainer = DarkGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreenLight,
    onPrimary = SageGreenDark,
    primaryContainer = SageGreen,
    onPrimaryContainer = SurfaceWhite,
    
    secondary = SageGreen,
    onSecondary = SurfaceWhite,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = SageGreenLight,
    
    tertiary = SoftGold,
    onTertiary = SoftBlack,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = SoftGoldLight,
    
    background = DarkBackground,
    onBackground = DarkOnBackground,
    
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightGray,
    
    outline = MediumGray,
    outlineVariant = DarkGray,
    
    // No error/red colors in dark theme either
    error = SoftGold,
    onError = SoftBlack,
    errorContainer = DarkSurfaceVariant,
    onErrorContainer = SoftGoldLight,
)

@Composable
fun SeekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+, disabled by default for consistent calm branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
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
        typography = SeekTypography,
        shapes = SeekShapes,
        content = content
    )
}
