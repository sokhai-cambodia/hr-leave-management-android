package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.DangerColor

/**
 * Consistent full-space error state (icon + message + Retry) replacing the ad-hoc
 * Column+Text+Button built per screen across Phases 3-11 (Task 13.4) — pure UI-layer swap.
 */
@Composable
fun ErrorStateView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppSpacing.xl),
        ) {
            Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null, tint = DangerColor)
            Spacer(Modifier.height(AppSpacing.sm))
            Text(text = message, color = DangerColor, textAlign = TextAlign.Center)
            Spacer(Modifier.height(AppSpacing.md))
            AppOutlinedButton(text = "Retry", onClick = onRetry, contentColor = DangerColor)
        }
    }
}

/** Consistent full-space empty state (icon + message), same slot every list screen falls back to. */
@Composable
fun EmptyStateView(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Inbox,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppSpacing.xl),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.height(AppSpacing.sm))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
