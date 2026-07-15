package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation

/**
 * A dashboard stat tile: tinted circular icon badge (per the ui.webp moodboard's colored-badge
 * treatment) over a bold, large numeral and label — used for "Available Days"/"Approvals".
 */
@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(CardCornerRadius)
    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(all = AppSpacing.lg)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = tint.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }
            Spacer(Modifier.height(AppSpacing.md))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        ) { content() }
    }
}
