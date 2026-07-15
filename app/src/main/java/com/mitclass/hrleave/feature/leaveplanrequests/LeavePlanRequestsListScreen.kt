package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto

@Composable
fun LeavePlanRequestsListScreen(
    onItemClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: LeavePlanRequestsListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    OnResume(onResume = viewModel::load)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Filled.Add, contentDescription = "New leave plan request")
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val current = state) {
                is LeavePlanRequestsListUiState.Loading -> LoadingBox()
                is LeavePlanRequestsListUiState.Error -> ErrorBox(message = current.message, onRetry = viewModel::load)
                is LeavePlanRequestsListUiState.Loaded -> {
                    if (current.requests.isEmpty()) {
                        EmptyBox()
                    } else {
                        LeavePlanRequestsList(
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
private fun LeavePlanRequestsList(
    requests: List<LeavePlanRequestDto>,
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
            LeavePlanRequestRow(request = request, onClick = { onItemClick(request.id) })
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
private fun LeavePlanRequestRow(request: LeavePlanRequestDto, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = request.leaveType.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${request.amount.toInt()} date(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusChip(status = request.status)
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No leave plan requests yet", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
        }
    }
}
