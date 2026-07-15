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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.ui.DatePickerField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeavePlanRequestFormScreen(
    onSaved: () -> Unit,
    onSubmittedSuccess: (String) -> Unit = {},
    viewModel: LeavePlanRequestFormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }
    LaunchedEffect(state.submittedRequestId) {
        state.submittedRequestId?.let(onSubmittedSuccess)
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = if (viewModel.isEditMode) "Edit Leave Plan Request" else "New Leave Plan Request",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        val selectedType = state.leaveTypes.firstOrNull { it.id == state.selectedLeaveTypeId }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedType?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Leave type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                state.leaveTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            viewModel.onLeaveTypeSelected(type.id)
                            expanded = false
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        var pendingDate by remember { mutableStateOf<LocalDate?>(null) }
        DatePickerField(
            label = "Add a date",
            date = pendingDate,
            onDateSelected = { date ->
                pendingDate = date
                viewModel.addDate(date)
            },
        )
        state.duplicateDateMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(12.dp))

        if (state.dates.isNotEmpty()) {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                state.dates.forEach { date ->
                    InputChip(
                        selected = false,
                        onClick = { viewModel.removeDate(date) },
                        label = { Text(date.toString()) },
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.removeDate(date) },
                                modifier = Modifier.height(18.dp),
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Remove $date")
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth(),
        )

        state.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }

        Spacer(Modifier.height(24.dp))
        if (viewModel.isPrefilled && !viewModel.isEditMode) {
            // Task 6.3: the AI-recommendation entry point offers a one-tap "Submit now"
            // alongside the regular draft save.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = viewModel::save,
                    enabled = state.canSave,
                    modifier = Modifier.weight(1f),
                ) { Text("Save draft") }
                Button(
                    onClick = viewModel::saveAndSubmit,
                    enabled = state.canSave,
                    modifier = Modifier.weight(1f),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Submit now")
                    }
                }
            }
        } else {
            Button(
                onClick = viewModel::save,
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (viewModel.isEditMode) "Save changes" else "Save draft")
                }
            }
        }
    }
}
