package com.mitclass.hrleave.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// SPEC.md §7: buttons/text fields 12dp corners, cards 14dp corners.
val HrLeaveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

val ButtonCornerRadius = 12.dp
val ButtonMinHeight = 52.dp
val CardCornerRadius = 14.dp
val TextFieldCornerRadius = 12.dp
