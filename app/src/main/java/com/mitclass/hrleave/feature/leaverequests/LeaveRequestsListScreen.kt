package com.mitclass.hrleave.feature.leaverequests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.EmptyStateView
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.SearchSortBar
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.core.ui.StatusFilterRow
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto

/**
 * The one create entry point is the shell's global center FAB (Task 14.2) — this list has no
 * FAB of its own, matching the Flutter client (a single create affordance, not a duplicate one).
 */
@Composable
fun LeaveRequestsListScreen(
    onItemClick: (String) -> Unit,
    viewModel: LeaveRequestsListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortAscending by viewModel.sortAscending.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    OnResume(onResume = viewModel::load)

    Column(modifier = Modifier.fillMaxSize()) {
        SearchSortBar(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            sortAscending = sortAscending,
            onToggleSort = viewModel::toggleSortDirection,
            modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        )
        StatusFilterRow(
            selected = statusFilter,
            onSelect = viewModel::onStatusFilterSelected,
            modifier = Modifier.padding(horizontal = AppSpacing.lg),
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Box(modifier = Modifier.fillMaxSize()) {
            when (val current = state) {
                is LeaveRequestsListUiState.Loading -> LoadingBox()
                is LeaveRequestsListUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)
                is LeaveRequestsListUiState.Loaded -> {
                    val visibleRequests = viewModel.visibleRequests(current.requests)
                    if (visibleRequests.isEmpty()) {
                        EmptyStateView(message = stringResource(R.string.leave_requests_empty))
                    } else {
                        LeaveRequestsList(
                            requests = visibleRequests,
                            canLoadMore = current.canLoadMore,
                            isLoadingMore = current.isLoadingMore,
                            onItemClick = onItemClick,
                            onLoadMore = viewModel::loadMore,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaveRequestsList(
    requests: List<LeaveRequestDto>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onItemClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
    ) {
        itemsIndexed(requests, key = { _, item -> item.id }) { index, request ->
            LeaveRequestRow(request = request, onClick = { onItemClick(request.id) })
            if (index != requests.lastIndex) HorizontalDivider()
        }
        if (canLoadMore) {
            item {
                LaunchedEffect(Unit) { onLoadMore() }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isLoadingMore) CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LeaveRequestRow(request: LeaveRequestDto, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = request.leaveType.name, style = MaterialTheme.typography.titleMedium)
            StatusChip(status = request.status)
        }
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
            Text(
                text = stringResource(R.string.leave_detail_duration_value, request.startDate, request.endDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = stringResource(R.string.leave_common_days_value, request.amount),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
