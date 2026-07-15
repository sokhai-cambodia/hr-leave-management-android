package com.mitclass.hrleave.core.theme

import androidx.compose.ui.graphics.Color

// Verified against the Flutter sibling app's actual design system (tasks/plan.md Phase 13),
// which supersedes the stale SPEC.md §7 table.
val BrandPrimary = Color(0xFFE23744)
val BrandPrimaryDark = Color(0xFFC01F2B)

val DangerColor = Color(0xFFEF4444)
val WarningColor = Color(0xFFF5A623)
val SuccessColor = Color(0xFF22A659)
val InfoColor = Color(0xFF4C8DFF)

// STYLE_GUIDE.md is the source of truth (supersedes the earlier cream-tinted background): flat
// white for both background and surface in light mode.
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFFFFFF)
val LightFieldFill = Color(0xFFF7F7F9)
val LightBorder = Color(0xFFEAEAEE)

val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)

/**
 * STYLE_GUIDE.md "status pastel formula": derive a badge/stat-card background by blending the
 * status color at 14% alpha over white, using the full-saturation color as the foreground. One
 * shared helper instead of hand-picked bg/fg pairs per status.
 */
fun Color.pastelContainer(): Color = this.copy(alpha = 0.14f)
