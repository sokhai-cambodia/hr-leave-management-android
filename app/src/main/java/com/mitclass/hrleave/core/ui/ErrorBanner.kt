package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.TextFieldCornerRadius

/**
 * A tinted error banner (10% background, 30% border, error_outline icon + text) replacing the
 * plain red [Text] used per-form across Phases 1-6 (Task 13.6).
 */
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TextFieldCornerRadius),
        color = DangerColor.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, DangerColor.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null, tint = DangerColor)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = DangerColor,
                modifier = Modifier.padding(start = AppSpacing.sm),
            )
        }
    }
}
