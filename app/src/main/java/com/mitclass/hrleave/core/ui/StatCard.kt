package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius

/**
 * A dashboard stat tile: colored label + a bold, large numeral in the same tint, matching the
 * Flutter client's actual "Available Days"/"Approvals" cards (`ui/home.jpg`) — no icon badge, a
 * trailing chevron only when tappable.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(CardCornerRadius)
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.padding(all = AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = tint, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(AppSpacing.xs))
                Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = tint)
            }
            if (onClick != null) {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = tint)
            }
        }
    }
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.12f)),
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.12f)),
        ) { content() }
    }
}
