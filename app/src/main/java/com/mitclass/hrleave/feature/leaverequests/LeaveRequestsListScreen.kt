package com.mitclass.hrleave.feature.leaverequests

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
import com.mitclass.hrleave.core.ui.EmptyStateView
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto

@Composable
fun LeaveRequestsListScreen(
    onItemClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: LeaveRequestsListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    OnResume(onResume = viewModel::load)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Filled.Add, contentDescription = "New leave request")
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val current = state) {
                is LeaveRequestsListUiState.Loading -> LoadingBox()
                is LeaveRequestsListUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)
                is LeaveRequestsListUiState.Loaded -> {
                    if (current.requests.isEmpty()) {
                        EmptyStateView(message = "No leave requests yet")
                    } else {
                        LeaveRequestsList(
                            requests = current.requests,
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(requests, key = { it.id }) { request ->
            LeaveRequestRow(request = request, onClick = { onItemClick(request.id) })
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
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
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
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "${request.startDate} → ${request.endDate} · ${request.amount} day(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
