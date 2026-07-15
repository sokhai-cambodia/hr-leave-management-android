package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.theme.WarningColor

/** Renders a leave-request/leave-plan-request status ("draft"|"pending"|"approved"|"rejected") as a colored chip. */
@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Text(
        text = status.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

private fun statusColor(status: String): Color = when (status) {
    "approved" -> SuccessColor
    "pending" -> WarningColor
    "rejected" -> DangerColor
    else -> Color.Gray
}
