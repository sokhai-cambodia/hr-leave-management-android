package com.mitclass.hrleave.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.InfoColor
import com.mitclass.hrleave.core.theme.WarningColor
import com.mitclass.hrleave.core.ui.EmptyStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.PastelActionTile
import com.mitclass.hrleave.core.ui.StatCard
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
    onRequestLeaveClick: () -> Unit = {},
    onPlanLeaveClick: () -> Unit = {},
    pendingApprovalsViewModel: PendingApprovalsViewModel = hiltViewModel(),
    leaveBalancesViewModel: LeaveBalancesViewModel = hiltViewModel(),
) {
    LaunchedEffect(isApprover) {
        if (isApprover) pendingApprovalsViewModel.loadIfNeeded()
    }
    // Balances change as a side effect of actions taken on other screens (e.g. submitting a
    // leave request debits them) - refresh on every return to the dashboard, not just once.
    OnResume(onResume = leaveBalancesViewModel::load)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg),
    ) {
        ProfileHeader(user = user)
        Spacer(Modifier.height(AppSpacing.lg))
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            PastelActionTile(
                icon = Icons.AutoMirrored.Filled.FactCheck,
                label = "Request Leave",
                tint = BrandPrimary,
                onClick = onRequestLeaveClick,
                modifier = Modifier.weight(1f),
            )
            PastelActionTile(
                icon = Icons.AutoMirrored.Filled.EventNote,
                label = "Plan Leave",
                tint = WarningColor,
                onClick = onPlanLeaveClick,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(AppSpacing.md))
        val balancesState by leaveBalancesViewModel.uiState.collectAsState()
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            StatCard(
                label = "Available Days",
                value = availableDaysValue(balancesState),
                tint = InfoColor,
                modifier = Modifier.weight(1f),
            )
            if (isApprover) {
                val approvalsState by pendingApprovalsViewModel.uiState.collectAsState()
                StatCard(
                    label = "Approvals",
                    value = approvalsValue(approvalsState),
                    tint = WarningColor,
                    onClick = onPendingApprovalsClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        LeaveBalancesSection(state = balancesState, onRetry = { leaveBalancesViewModel.load() })
        if (quickActions.isNotEmpty()) {
            Text(
                text = "Quick actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = AppSpacing.xl, bottom = AppSpacing.sm),
            )
            Column {
                quickActions.forEachIndexed { index, action ->
                    QuickActionRow(action = action, onClick = { onQuickActionClick(action) })
                    if (index != quickActions.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

private fun availableDaysValue(state: LeaveBalancesUiState): String = when (state) {
    is LeaveBalancesUiState.Loaded -> formatBalance(state.balances.sumOf { it.availableBalance })
    else -> "–"
}

private fun approvalsValue(state: PendingApprovalsUiState): String = when (state) {
    is PendingApprovalsUiState.Loaded -> state.total.toString()
    else -> "–"
}

private fun initials(fullName: String?, email: String): String {
    val source = fullName?.takeIf { it.isNotBlank() } ?: email
    val parts = source.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "?"
    }
}

@Composable
private fun ProfileHeader(user: UserDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = BrandPrimary.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials(user.fullName, user.email),
                style = MaterialTheme.typography.titleMedium,
                color = BrandPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(AppSpacing.md))
        Column {
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
private fun LeaveBalancesSection(state: LeaveBalancesUiState, onRetry: () -> Unit) {
    Text(
        text = "Leave Balances",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = AppSpacing.xl, bottom = AppSpacing.sm),
    )
    when (state) {
        is LeaveBalancesUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        is LeaveBalancesUiState.Error -> Column {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry) { Text("Retry") }
        }
        is LeaveBalancesUiState.Loaded -> {
            if (state.balances.isEmpty()) {
                EmptyStateView(message = "No leave balances yet")
            } else {
                Column {
                    state.balances.forEachIndexed { index, balance ->
                        LeaveBalanceRow(balance)
                        if (index != state.balances.lastIndex) HorizontalDivider()
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
            .padding(vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${balance.leaveType.name} (${balance.leaveType.code})",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Balance ${formatBalance(balance.balance)} · Taken ${formatBalance(balance.takenBalance)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatBalance(balance.availableBalance),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatBalance(value: Double): String =
    if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

@Composable
private fun QuickActionRow(action: QuickAction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = action.icon, contentDescription = null)
        Text(
            text = action.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = AppSpacing.md),
        )
        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
    }
}
