package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing

/**
 * Search field + sort-direction toggle, matching the same row already used by the admin generic
 * CRUD list screens (Task 10.1) — reused here so the end-user Leave Requests / Leave Plan
 * Requests / Approvals lists get the same search+sort affordance as admin master-data lists.
 */
@Composable
fun SearchSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortAscending: Boolean,
    onToggleSort: () -> Unit,
    modifier: Modifier = Modifier,
    searchLabel: String = stringResource(R.string.common_label_search),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = searchLabel,
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onToggleSort, modifier = Modifier.padding(start = AppSpacing.xs)) {
            Icon(
                imageVector = if (sortAscending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                contentDescription = if (sortAscending) {
                    stringResource(R.string.admin_sort_ascending_content_desc)
                } else {
                    stringResource(R.string.admin_sort_descending_content_desc)
                },
            )
        }
    }
}
