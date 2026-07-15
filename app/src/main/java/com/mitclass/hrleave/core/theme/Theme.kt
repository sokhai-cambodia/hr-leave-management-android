package com.mitclass.hrleave.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = InfoColor,
    tertiary = SuccessColor,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightFieldFill,
    outline = LightBorder,
    error = DangerColor,
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = InfoColor,
    tertiary = SuccessColor,
    background = DarkBackground,
    surface = DarkSurface,
    error = DangerColor,
)

/**
 * SPEC.md §7: light-first by default, dark mode available as an explicit toggle rather than
 * following the system setting automatically.
 */
@Composable
fun HrLeaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HrLeaveTypography,
        shapes = HrLeaveShapes,
        content = content,
    )
}
