package com.mitclass.hrleave.feature.dashboard

import androidx.compose.ui.graphics.vector.ImageVector

/** A dashboard quick-action tile. Only added once its target screen is real — see tasks/plan.md. */
data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val route: String,
)
