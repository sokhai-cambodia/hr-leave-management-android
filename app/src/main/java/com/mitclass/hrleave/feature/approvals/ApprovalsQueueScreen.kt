package com.mitclass.hrleave.feature.approvals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto

@Composable
fun ApprovalsQueueScreen(
    viewModel: ApprovalsQueueViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val processingIds by viewModel.processingIds.collectAsState()
    val actionError by viewModel.actionError.collectAsState()
    OnResume(onResume = viewModel::load)

    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingReject by remember { mutableStateOf<PendingReject?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Leave Requests") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Leave Plans") })
        }
        actionError?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                TextButton(onClick = viewModel::dismissActionError) { Text("Dismiss") }
            }
        }

        when (val current = state) {
            is ApprovalsQueueUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is ApprovalsQueueUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = current.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
                }
            }

            is ApprovalsQueueUiState.Loaded -> {
                if (selectedTab == 0) {
                    LeaveRequestsTab(
                        requests = current.leaveRequests,
                        processingIds = processingIds,
                        onApprove = viewModel::approveLeaveRequest,
                        onReject = { id -> pendingReject = PendingReject.LeaveRequest(id) },
                    )
                } else {
                    LeavePlanRequestsTab(
                        requests = current.leavePlanRequests,
                        processingIds = processingIds,
                        onApprove = viewModel::approveLeavePlanRequest,
                        onReject = { id -> pendingReject = PendingReject.LeavePlanRequest(id) },
                    )
                }
            }
        }
    }

    pendingReject?.let { reject ->
        AlertDialog(
            onDismissRequest = { pendingReject = null },
            title = { Text("Reject this request?") },
            text = { Text("The submitter will be notified.") },
            confirmButton = {
                TextButton(onClick = {
                    when (reject) {
                        is PendingReject.LeaveRequest -> viewModel.rejectLeaveRequest(reject.id)
                        is PendingReject.LeavePlanRequest -> viewModel.rejectLeavePlanRequest(reject.id)
                    }
                    pendingReject = null
                }) { Text("Reject") }
            },
            dismissButton = {
                TextButton(onClick = { pendingReject = null }) { Text("Cancel") }
            },
        )
    }
}

private sealed interface PendingReject {
    data class LeaveRequest(val id: String) : PendingReject
    data class LeavePlanRequest(val id: String) : PendingReject
}

@Composable
private fun LeaveRequestsTab(
    requests: List<LeaveRequestDto>,
    processingIds: Set<String>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    if (requests.isEmpty()) {
        EmptyBox("No pending leave requests")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(requests, key = { it.id }) { request ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = request.owner.fullName ?: request.owner.email, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${request.leaveType.name} · ${request.startDate} → ${request.endDate} · ${request.amount} day(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ApprovalActions(
                        isProcessing = request.id in processingIds,
                        onApprove = { onApprove(request.id) },
                        onReject = { onReject(request.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LeavePlanRequestsTab(
    requests: List<LeavePlanRequestDto>,
    processingIds: Set<String>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    if (requests.isEmpty()) {
        EmptyBox("No pending leave plan requests")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(requests, key = { it.id }) { request ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = request.owner.fullName ?: request.owner.email, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${request.leaveType.name} · ${request.amount.toInt()} date(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ApprovalActions(
                        isProcessing = request.id in processingIds,
                        onApprove = { onApprove(request.id) },
                        onReject = { onReject(request.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ApprovalActions(isProcessing: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(onClick = onApprove, enabled = !isProcessing) { Text("Approve") }
        OutlinedButton(onClick = onReject, enabled = !isProcessing) { Text("Reject") }
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.padding(start = 4.dp).size(20.dp))
        }
    }
}

@Composable
private fun EmptyBox(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}
