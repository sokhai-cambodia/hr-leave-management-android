package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.PillCornerRadius
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.theme.WarningColor

/**
 * Renders a leave-request/leave-plan-request status ("draft"|"pending"|"approved"|"rejected") as
 * an outlined ALL-CAPS pill chip (tinted fill + colored border), matching the Flutter client's
 * actual status badges (`ui/leave-list.jpg`) rather than a solid-fill chip.
 */
@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    val shape = RoundedCornerShape(PillCornerRadius)
    Text(
        text = status.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .background(color = color.copy(alpha = 0.1f), shape = shape)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.4f)), shape = shape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

private fun statusColor(status: String): Color = when (status) {
    "approved" -> SuccessColor
    "pending" -> WarningColor
    "rejected" -> DangerColor
    else -> Color.Gray
}
