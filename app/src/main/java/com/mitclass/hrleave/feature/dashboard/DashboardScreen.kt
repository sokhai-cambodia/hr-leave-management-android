package com.mitclass.hrleave.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
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
        ProfileCard(user = user)
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
                icon = Icons.Filled.CalendarToday,
                label = "Available Days",
                value = availableDaysValue(balancesState),
                tint = InfoColor,
                modifier = Modifier.weight(1f),
            )
            if (isApprover) {
                val approvalsState by pendingApprovalsViewModel.uiState.collectAsState()
                StatCard(
                    icon = Icons.Filled.Approval,
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
                modifier = Modifier.padding(top = AppSpacing.xl, bottom = AppSpacing.md),
            )
            QuickActionsGrid(quickActions = quickActions, onClick = onQuickActionClick)
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
private fun ProfileCard(user: UserDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.lg),
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
}

@Composable
private fun LeaveBalancesSection(state: LeaveBalancesUiState, onRetry: () -> Unit) {
    Text(
        text = "Leave Balances",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = AppSpacing.xl, bottom = AppSpacing.md),
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(CardCornerRadius),
                    elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
                ) {
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
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
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
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        quickActions.chunked(2).forEach { rowActions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
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
        shape = RoundedCornerShape(CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(imageVector = action.icon, contentDescription = null)
            Text(text = action.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
