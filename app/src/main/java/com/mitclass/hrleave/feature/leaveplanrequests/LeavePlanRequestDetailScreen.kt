package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.core.ui.StickyBottomActionPanel
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.feature.leaverequests.IconDetailRow
import com.mitclass.hrleave.feature.leaverequests.TimelineRow

@Composable
fun LeavePlanRequestDetailScreen(
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: LeavePlanRequestDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    OnResume(onResume = viewModel::load)
    val deleteState by viewModel.deleteState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(deleteState) {
        if (deleteState is PlanDeleteState.Deleted) onDeleted()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val current = state) {
            is LeavePlanRequestDetailUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is LeavePlanRequestDetailUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)

            is LeavePlanRequestDetailUiState.Loaded -> LeavePlanRequestDetailContent(
                request = current.request,
                deleteState = deleteState,
                submitState = submitState,
                onEdit = { onEdit(current.request.id) },
                onDeleteClick = { showDeleteConfirm = true },
                onSubmitClick = viewModel::submit,
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.leave_detail_dialog_delete_plan_request_title)) },
            text = { Text(stringResource(R.string.common_dialog_irreversible_warning)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.common_action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.common_action_cancel)) }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LeavePlanRequestDetailContent(
    request: LeavePlanRequestDto,
    deleteState: PlanDeleteState,
    submitState: PlanSubmitState,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = request.leaveType.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                StatusChip(status = request.status)
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(AppSpacing.lg))

            IconDetailRow(
                icon = Icons.Outlined.Schedule,
                label = stringResource(R.string.leave_common_requested_days_label),
                value = stringResource(R.string.leave_common_days_value, request.amount.toInt()),
            )
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            Text(
                text = stringResource(R.string.leaveplan_detail_dates_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                request.details.map { it.leaveDate }.sorted().forEach { date ->
                    AssistChip(onClick = {}, label = { Text(date) })
                }
            }

            request.description?.takeIf { it.isNotBlank() }?.let { description ->
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                Text(text = stringResource(R.string.leave_common_description_label), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(text = description, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))
            Text(text = stringResource(R.string.leave_common_timeline_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            TimelineRow(label = stringResource(R.string.leave_common_timeline_created_by), value = request.owner.fullName ?: request.owner.email)
            TimelineRow(label = stringResource(R.string.leave_common_timeline_created_at), value = request.requestedAt)
            request.submittedAt?.let { TimelineRow(label = stringResource(R.string.leave_common_timeline_submitted_at), value = it) }
            request.approver?.let { TimelineRow(label = stringResource(R.string.leave_common_timeline_line_approver), value = it.fullName ?: it.email) }
            request.approvalAt?.let { TimelineRow(label = stringResource(R.string.leave_common_timeline_approved_at), value = it) }

            if (deleteState is PlanDeleteState.Error) {
                Text(
                    text = deleteState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = AppSpacing.md),
                )
            }
            if (submitState is PlanSubmitState.Error) {
                Text(
                    text = submitState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = AppSpacing.md),
                )
            }
        }

        if (request.status == "draft") {
            StickyBottomActionPanel {
                AppButton(
                    text = stringResource(R.string.common_action_submit),
                    onClick = onSubmitClick,
                    enabled = submitState !is PlanSubmitState.Submitting,
                    loading = submitState is PlanSubmitState.Submitting,
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    AppOutlinedButton(text = stringResource(R.string.common_action_edit), onClick = onEdit, modifier = Modifier.weight(1f))
                    AppOutlinedButton(
                        text = stringResource(R.string.common_action_delete),
                        onClick = onDeleteClick,
                        enabled = deleteState !is PlanDeleteState.Deleting,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
