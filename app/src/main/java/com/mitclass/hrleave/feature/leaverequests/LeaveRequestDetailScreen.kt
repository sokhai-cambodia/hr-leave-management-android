package com.mitclass.hrleave.feature.leaverequests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.ui.StatusChip
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto

@Composable
fun LeaveRequestDetailScreen(
    viewModel: LeaveRequestDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val current = state) {
            is LeaveRequestDetailUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is LeaveRequestDetailUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = current.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
                }
            }

            is LeaveRequestDetailUiState.Loaded -> LeaveRequestDetailContent(current.request)
        }
    }
}

@Composable
private fun LeaveRequestDetailContent(request: LeaveRequestDto) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = request.leaveType.name, style = MaterialTheme.typography.headlineSmall)
            StatusChip(status = request.status)
        }
        Spacer(modifier = Modifier.height(8.dp))
        DetailRow(label = "Dates", value = "${request.startDate} → ${request.endDate}")
        DetailRow(label = "Days", value = request.amount.toString())
        request.description?.takeIf { it.isNotBlank() }?.let {
            DetailRow(label = "Description", value = it)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        DetailRow(label = "Requested by", value = request.owner.fullName ?: request.owner.email)
        DetailRow(label = "Requested at", value = request.requestedAt)
        request.submittedAt?.let { DetailRow(label = "Submitted at", value = it) }
        request.approver?.let { DetailRow(label = "Approver", value = it.fullName ?: it.email) }
        request.approvalAt?.let { DetailRow(label = "Decided at", value = it) }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
