package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing

private val statusOptions = listOf(null, "draft", "pending", "approved", "rejected")

@Composable
private fun statusLabel(status: String?): String = when (status) {
    null -> stringResource(R.string.common_status_all)
    "draft" -> stringResource(R.string.common_status_draft)
    "pending" -> stringResource(R.string.common_status_pending)
    "approved" -> stringResource(R.string.common_status_approved)
    "rejected" -> stringResource(R.string.common_status_rejected)
    else -> status
}

/** Status filter chips shared by the Leave Requests and Leave Plan Requests lists. */
@Composable
fun StatusFilterRow(
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        statusOptions.forEach { status ->
            FilterChip(
                selected = status == selected,
                onClick = { onSelect(status) },
                label = { Text(statusLabel(status)) },
            )
        }
    }
}
