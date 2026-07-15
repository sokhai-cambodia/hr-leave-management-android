package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing

/**
 * A bottom-docked panel with a soft top shadow and nav-bar-safe padding, holding draft-only
 * actions on detail screens (Submit/Edit/Delete) — Task 13.4/13.5.
 */
@Composable
fun StickyBottomActionPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            content = content,
        )
    }
}
