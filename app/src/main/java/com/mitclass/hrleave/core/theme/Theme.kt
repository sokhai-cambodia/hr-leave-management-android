package com.mitclass.hrleave.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Two things Material3's Material You tonal system does that fight STYLE_GUIDE.md's flat,
// single-surface design:
// 1. surfaceTint defaults to `primary` (brand red) and gets blended over every elevated
//    Surface/Card/BottomAppBar/TopAppBar at an alpha proportional to tonalElevation. Setting it
//    to `Color.Transparent` does NOT disable this — M3's surfaceColorAtElevation overrides the
//    tint's alpha channel, so Transparent's black RGB still shows through as a gray wash.
//    Matching surfaceTint to the surface color itself makes the blend a no-op instead.
// 2. Card's default containerColor isn't `colorScheme.surface`, it's `surfaceContainerLowest` —
//    a role `lightColorScheme()`/`darkColorScheme()` auto-derive from the primary/neutral seed
//    when left unset, coming out as a muddy mauve-gray tinted by our red primary. Pinning every
//    surfaceContainer* role to the flat surface color keeps unstyled Cards genuinely white.
private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = InfoColor,
    tertiary = SuccessColor,
    background = LightBackground,
    surface = LightSurface,
    surfaceTint = LightSurface,
    surfaceVariant = LightFieldFill,
    surfaceDim = LightSurface,
    surfaceBright = LightSurface,
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightSurface,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurface,
    surfaceContainerHighest = LightSurface,
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
    surfaceTint = DarkSurface,
    surfaceDim = DarkSurface,
    surfaceBright = DarkSurface,
    surfaceContainerLowest = DarkSurface,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurface,
    surfaceContainerHighest = DarkSurface,
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
