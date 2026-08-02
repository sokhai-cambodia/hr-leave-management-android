package com.mitclass.hrleave.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.InfoColor
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.theme.WarningColor
import com.mitclass.hrleave.core.ui.BarChartEntry
import com.mitclass.hrleave.core.ui.EmptyStateView
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.SimpleBarChart
import com.mitclass.hrleave.core.ui.StatCard
import java.util.Locale

@Composable
fun ReportsScreen(
    isApprover: Boolean,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(isApprover) { viewModel.load(isApprover) }
    OnResume(onResume = { viewModel.load(isApprover) })

    when (val current = state) {
        is ReportsUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is ReportsUiState.Error -> ErrorStateView(message = current.message, onRetry = { viewModel.load(isApprover) })
        is ReportsUiState.Loaded -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.lg),
            ) {
                PersonalReportSection(current.personal)
                if (current.team != null) {
                    Spacer(Modifier.height(AppSpacing.xl))
                    HorizontalDivider()
                    Spacer(Modifier.height(AppSpacing.lg))
                    TeamReportSection(current.team)
                }
            }
        }
    }
}

@Composable
private fun PersonalReportSection(report: PersonalReport) {
    Text(text = stringResource(R.string.reports_personal_section_title), style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(AppSpacing.md))
    if (report.balances.isEmpty()) {
        EmptyStateView(message = stringResource(R.string.reports_personal_empty))
    } else {
        SimpleBarChart(
            entries = report.balances.map { balance ->
                val total = balance.takenDays + balance.availableDays
                val fraction = if (total > 0) (balance.takenDays / total).toFloat() else 0f
                BarChartEntry(
                    label = balance.leaveTypeName,
                    value = fraction,
                    valueText = "%.0f/%.0f".format(balance.takenDays, total),
                )
            },
            barColor = BrandPrimary,
        )
    }
    Spacer(Modifier.height(AppSpacing.lg))
    Text(text = stringResource(R.string.reports_status_breakdown_title), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(AppSpacing.sm))
    StatusCountsRow(report.statusCounts)
}

@Composable
private fun TeamReportSection(report: TeamReport) {
    Text(text = stringResource(R.string.reports_team_section_title), style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(AppSpacing.md))

    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm), modifier = Modifier.fillMaxWidth()) {
        StatCard(
            label = stringResource(R.string.reports_team_stat_total),
            value = report.totalCount.toString(),
            tint = BrandPrimary,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(R.string.reports_team_stat_pending),
            value = (report.statusCounts["pending"] ?: 0).toString(),
            tint = WarningColor,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(AppSpacing.sm))
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm), modifier = Modifier.fillMaxWidth()) {
        StatCard(
            label = stringResource(R.string.reports_team_stat_approved),
            value = (report.statusCounts["approved"] ?: 0).toString(),
            tint = SuccessColor,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(R.string.reports_team_stat_rejected),
            value = (report.statusCounts["rejected"] ?: 0).toString(),
            tint = DangerColor,
            modifier = Modifier.weight(1f),
        )
    }

    Spacer(Modifier.height(AppSpacing.lg))
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.reports_team_turnaround_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = report.avgApprovalTurnaroundHours?.let {
                stringResource(R.string.reports_team_turnaround_value, it)
            } ?: stringResource(R.string.reports_team_turnaround_empty),
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    Spacer(Modifier.height(AppSpacing.lg))
    Text(text = stringResource(R.string.reports_team_usage_title), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(AppSpacing.sm))
    if (report.approvedDaysByLeaveType.isEmpty()) {
        EmptyStateView(message = stringResource(R.string.reports_team_usage_empty))
    } else {
        SimpleBarChart(
            entries = report.approvedDaysByLeaveType.map { (name, days) ->
                BarChartEntry(label = name, value = days.toFloat(), valueText = "%.0f d".format(days))
            },
            barColor = InfoColor,
        )
    }

    Spacer(Modifier.height(AppSpacing.lg))
    Text(text = stringResource(R.string.reports_team_trend_title), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(AppSpacing.sm))
    if (report.monthlyTrend.isEmpty()) {
        EmptyStateView(message = stringResource(R.string.reports_team_usage_empty))
    } else {
        SimpleBarChart(
            entries = report.monthlyTrend.map { (month, count) ->
                BarChartEntry(label = month, value = count.toFloat(), valueText = count.toString())
            },
            barColor = BrandPrimary,
            labelWidth = 48.dp,
        )
    }

    Spacer(Modifier.height(AppSpacing.lg))
    Text(text = stringResource(R.string.reports_team_currently_out_title), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(AppSpacing.sm))
    if (report.currentlyOut.isEmpty()) {
        EmptyStateView(message = stringResource(R.string.reports_team_currently_out_empty))
    } else {
        Column {
            report.currentlyOut.forEach { entry ->
                Text(
                    text = stringResource(
                        R.string.reports_team_currently_out_row,
                        entry.name,
                        entry.leaveTypeName,
                        entry.returnsOn,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = AppSpacing.xs),
                )
            }
        }
    }
}

@Composable
private fun StatusCountsRow(statusCounts: Map<String, Int>) {
    val statuses = listOf(
        "draft" to R.string.common_status_draft,
        "pending" to R.string.common_status_pending,
        "approved" to R.string.common_status_approved,
        "rejected" to R.string.common_status_rejected,
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        statuses.forEach { (status, labelRes) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = (statusCounts[status] ?: 0).toString(), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun String.format(vararg args: Any): String = String.format(Locale.getDefault(), this, *args)
