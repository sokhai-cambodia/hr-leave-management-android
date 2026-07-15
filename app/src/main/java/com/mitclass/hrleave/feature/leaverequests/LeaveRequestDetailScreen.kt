package com.mitclass.hrleave.feature.leaverequests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.core.ui.OnResume
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.core.ui.StickyBottomActionPanel
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto

@Composable
fun LeaveRequestDetailScreen(
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: LeaveRequestDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    OnResume(onResume = viewModel::load)
    val deleteState by viewModel.deleteState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(deleteState) {
        if (deleteState is DeleteState.Deleted) onDeleted()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val current = state) {
            is LeaveRequestDetailUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is LeaveRequestDetailUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)

            is LeaveRequestDetailUiState.Loaded -> LeaveRequestDetailContent(
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
            title = { Text("Delete leave request?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun LeaveRequestDetailContent(
    request: LeaveRequestDto,
    deleteState: DeleteState,
    submitState: SubmitState,
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
                icon = Icons.Outlined.CalendarMonth,
                label = "Duration",
                value = "${request.startDate} to ${request.endDate}",
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
            IconDetailRow(
                icon = Icons.Outlined.Schedule,
                label = "Requested Days",
                value = "${request.amount} Days",
            )

            request.description?.takeIf { it.isNotBlank() }?.let { description ->
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                Text(text = "Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(text = description, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))
            Text(text = "Request Timeline & Workflow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            TimelineRow(label = "Created by", value = request.owner.fullName ?: request.owner.email)
            TimelineRow(label = "Created at", value = request.requestedAt)
            request.submittedAt?.let { TimelineRow(label = "Submitted at", value = it) }
            request.approver?.let { TimelineRow(label = "Line approver", value = it.fullName ?: it.email) }
            request.approvalAt?.let { TimelineRow(label = "Approved at", value = it) }

            if (deleteState is DeleteState.Error) {
                Text(
                    text = deleteState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = AppSpacing.md),
                )
            }
            if (submitState is SubmitState.Error) {
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
                    text = "Submit",
                    onClick = onSubmitClick,
                    enabled = submitState !is SubmitState.Submitting,
                    loading = submitState is SubmitState.Submitting,
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    AppOutlinedButton(text = "Edit", onClick = onEdit, modifier = Modifier.weight(1f))
                    AppOutlinedButton(
                        text = "Delete",
                        onClick = onDeleteClick,
                        enabled = deleteState !is DeleteState.Deleting,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/** Icon + gray label above a bold value — the "Duration"/"Requested Days" rows (`ui/detail_page.jpg`). */
@Composable
internal fun IconDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp).size(20.dp),
        )
        Column(modifier = Modifier.padding(start = AppSpacing.sm)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

/** Red-dot bullet + gray label on the left, bold value right-aligned — the workflow rows. */
@Composable
internal fun TimelineRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color = BrandPrimary, shape = CircleShape))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = AppSpacing.sm),
            )
        }
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
