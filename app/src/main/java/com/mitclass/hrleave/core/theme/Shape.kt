package com.mitclass.hrleave.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Verified against the Flutter sibling app (tasks/plan.md Phase 13).
val ButtonCornerRadius = 14.dp
val ButtonMinHeight = 54.dp
val CardCornerRadius = 18.dp
val TextFieldCornerRadius = 12.dp
val PillCornerRadius = 999.dp

// Soft, tinted card shadow depth (per ui.webp) instead of a bare 1dp hairline.
val CardElevation = 3.dp

val HrLeaveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(TextFieldCornerRadius),
    medium = RoundedCornerShape(ButtonCornerRadius),
    large = RoundedCornerShape(CardCornerRadius),
    extraLarge = RoundedCornerShape(20.dp),
)
