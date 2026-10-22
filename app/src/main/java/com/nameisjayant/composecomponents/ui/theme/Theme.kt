package com.nameisjayant.composecomponents.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = InkOnPrimary,
    primaryContainer = InkPrimaryContainer,
    onPrimaryContainer = InkOnPrimaryContainer,
    secondary = IndigoLight,
    secondaryContainer = InkSecondaryContainer,
    onSecondaryContainer = InkOnSecondaryContainer,
    tertiary = InkTertiary,
    tertiaryContainer = InkTertiaryContainer,
    background = InkBackground,
    onBackground = InkOnSurface,
    surface = InkSurface,
    onSurface = InkOnSurface,
    surfaceVariant = InkSurfaceHighest,
    onSurfaceVariant = InkOnSurfaceVariant,
    surfaceContainerLowest = InkSurfaceLowest,
    surfaceContainerLow = InkSurfaceLow,
    surfaceContainer = InkSurfaceContainer,
    surfaceContainerHigh = InkSurfaceHigh,
    surfaceContainerHighest = InkSurfaceHighest,
    surfaceTint = Color.Transparent,
    outline = InkOutline,
    outlineVariant = InkOutlineVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = PaperPrimaryContainer,
    onPrimaryContainer = PaperOnPrimaryContainer,
    secondary = Indigo,
    secondaryContainer = PaperSecondaryContainer,
    onSecondaryContainer = PaperOnSecondaryContainer,
    tertiary = PaperTertiary,
    tertiaryContainer = PaperTertiaryContainer,
    background = PaperBackground,
    onBackground = PaperOnSurface,
    surface = PaperSurface,
    onSurface = PaperOnSurface,
    surfaceVariant = PaperSurfaceHighest,
    onSurfaceVariant = PaperOnSurfaceVariant,
    surfaceContainerLowest = PaperSurfaceLowest,
    surfaceContainerLow = PaperSurfaceLow,
    surfaceContainer = PaperSurfaceContainer,
    surfaceContainerHigh = PaperSurfaceHigh,
    surfaceContainerHighest = PaperSurfaceHighest,
    surfaceTint = Color.Transparent,
    outline = PaperOutline,
    outlineVariant = PaperOutlineVariant,
)

@Composable
fun ComposeComponentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default so the demo shows the components on a known palette.
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
