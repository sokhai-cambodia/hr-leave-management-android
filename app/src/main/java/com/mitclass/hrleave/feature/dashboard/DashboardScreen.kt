package com.mitclass.hrleave.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.UserDto
import java.util.Locale

@Composable
fun DashboardScreen(
    user: UserDto,
    isApprover: Boolean = false,
    quickActions: List<QuickAction> = emptyList(),
    onQuickActionClick: (QuickAction) -> Unit = {},
    onPendingApprovalsClick: () -> Unit = {},
    pendingApprovalsViewModel: PendingApprovalsViewModel = hiltViewModel(),
    leaveBalancesViewModel: LeaveBalancesViewModel = hiltViewModel(),
) {
    LaunchedEffect(isApprover) {
        if (isApprover) pendingApprovalsViewModel.loadIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        ProfileCard(user = user)
        if (isApprover) {
            val approvalsState by pendingApprovalsViewModel.uiState.collectAsState()
            PendingApprovalsCard(state = approvalsState, onClick = onPendingApprovalsClick)
        }
        val balancesState by leaveBalancesViewModel.uiState.collectAsState()
        LeaveBalancesSection(state = balancesState, onRetry = { leaveBalancesViewModel.load() })
        if (quickActions.isNotEmpty()) {
            Text(
                text = "Quick actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            )
            QuickActionsGrid(quickActions = quickActions, onClick = onQuickActionClick)
        }
    }
}

@Composable
private fun ProfileCard(user: UserDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.fullName ?: user.email, style = MaterialTheme.typography.titleLarge)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = user.team?.name ?: "No team assigned",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PendingApprovalsCard(state: PendingApprovalsUiState, onClick: () -> Unit) {
    if (state is PendingApprovalsUiState.Error) return
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Filled.Approval, contentDescription = null)
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            ) {
                Text(text = "Pending Approvals", style = MaterialTheme.typography.titleMedium)
                when (state) {
                    is PendingApprovalsUiState.Loaded -> Text(
                        text = "${state.total} awaiting your review",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    else -> Unit
                }
            }
            if (state is PendingApprovalsUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            } else {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Composable
private fun LeaveBalancesSection(state: LeaveBalancesUiState, onRetry: () -> Unit) {
    Text(
        text = "Leave Balances",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
    )
    when (state) {
        is LeaveBalancesUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        is LeaveBalancesUiState.Error -> Column {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry) { Text("Retry") }
        }
        is LeaveBalancesUiState.Loaded -> {
            if (state.balances.isEmpty()) {
                Text(
                    text = "No leave balances yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        state.balances.forEachIndexed { index, balance ->
                            LeaveBalanceRow(balance)
                            if (index != state.balances.lastIndex) HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaveBalanceRow(balance: LeaveBalanceDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = balance.leaveType.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Balance ${formatBalance(balance.balance)} · Taken ${formatBalance(balance.takenBalance)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = formatBalance(balance.availableBalance),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private fun formatBalance(value: Double): String =
    if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

@Composable
private fun QuickActionsGrid(quickActions: List<QuickAction>, onClick: (QuickAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        quickActions.chunked(2).forEach { rowActions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowActions.forEach { action ->
                    Box(modifier = Modifier.weight(1f)) {
                        QuickActionTile(action = action, onClick = { onClick(action) })
                    }
                }
                if (rowActions.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionTile(action: QuickAction, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(imageVector = action.icon, contentDescription = null)
            Text(text = action.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
