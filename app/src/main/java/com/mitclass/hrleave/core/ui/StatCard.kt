package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
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
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
import com.mitclass.hrleave.core.theme.pastelContainer

/**
 * A dashboard stat tile: STYLE_GUIDE.md's "status pastel formula" container (tint @ 14% over
 * white) + a colored label and bold, large numeral in the same tint — no icon badge, a trailing
 * chevron only when tappable.
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
                Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = tint)
            }
        }
    }
    val colors = CardDefaults.cardColors(containerColor = tint.pastelContainer())
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        ) { content() }
    }
}
