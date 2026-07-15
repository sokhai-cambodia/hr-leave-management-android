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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.core.theme.TextFieldCornerRadius
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.DatePickerField
import com.mitclass.hrleave.core.ui.ErrorBanner
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestFormScreen(
    onSaved: () -> Unit,
    onSubmittedSuccess: (String) -> Unit = {},
    viewModel: LeaveRequestFormViewModel = hiltViewModel(),
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
            .padding(AppSpacing.lg),
    ) {
        var expanded by remember { mutableStateOf(false) }
        val selectedType = state.leaveTypes.firstOrNull { it.id == state.selectedLeaveTypeId }
        Column {
            Text(text = "Leave Type", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(AppSpacing.sm))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedType?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select a leave type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(TextFieldCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LightFieldFill,
                        unfocusedContainerColor = LightFieldFill,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    state.leaveTypes.forEach { type: LeaveTypeDto ->
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
        }
        Spacer(Modifier.height(AppSpacing.md))

        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            DatePickerField(
                label = "Start Date",
                date = state.startDate,
                onDateSelected = viewModel::onStartDateSelected,
                modifier = Modifier.weight(1f),
            )
            DatePickerField(
                label = "End Date",
                date = state.endDate,
                onDateSelected = viewModel::onEndDateSelected,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(AppSpacing.md))

        AppTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = "Description",
            placeholder = "Enter reason or extra notes (optional)...",
            singleLine = false,
            minLines = 3,
        )

        state.errorMessage?.let {
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.md))
        }

        Spacer(Modifier.height(AppSpacing.lg))
        if (viewModel.isEditMode) {
            AppButton(
                text = "Update",
                onClick = viewModel::save,
                enabled = state.canSave,
                loading = state.isSaving,
            )
        } else {
            AppButton(
                text = "Submit",
                onClick = viewModel::saveAndSubmit,
                enabled = state.canSave,
                loading = state.isSaving,
            )
            Spacer(Modifier.height(AppSpacing.sm))
            AppOutlinedButton(
                text = "Save as Draft",
                onClick = viewModel::save,
                enabled = state.canSave,
            )
        }
    }
}
