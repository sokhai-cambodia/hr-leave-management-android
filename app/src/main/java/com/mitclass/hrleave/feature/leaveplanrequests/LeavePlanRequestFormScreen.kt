package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
            .padding(AppSpacing.lg),
    ) {
        var expanded by remember { mutableStateOf(false) }
        val selectedType = state.leaveTypes.firstOrNull { it.id == state.selectedLeaveTypeId }
        Column {
            Text(text = "Leave type", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(AppSpacing.sm))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedType?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
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
        }
        Spacer(Modifier.height(AppSpacing.md))

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
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.xs))
        }
        Spacer(Modifier.height(AppSpacing.md))

        Text(text = "Selected dates", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AppSpacing.sm))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            shape = RoundedCornerShape(TextFieldCornerRadius),
            color = LightFieldFill,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Box(modifier = Modifier.padding(AppSpacing.md)) {
                if (state.dates.isEmpty()) {
                    Text(
                        text = "No dates added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        state.dates.forEach { date ->
                            InputChip(
                                selected = false,
                                onClick = { viewModel.removeDate(date) },
                                label = { Text(date.toString()) },
                                avatar = {
                                    Icon(
                                        Icons.Filled.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove $date",
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(AppSpacing.md))
        AppTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = "Description (optional)",
        )

        state.errorMessage?.let {
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.md))
        }

        Spacer(Modifier.height(AppSpacing.lg))
        if (viewModel.isPrefilled && !viewModel.isEditMode) {
            // Task 6.3: the AI-recommendation entry point offers a one-tap "Submit now"
            // alongside the regular draft save — the plan's create-mode submit hierarchy
            // (primary Submit over an outlined Save as Draft).
            AppButton(
                text = "Submit now",
                onClick = viewModel::saveAndSubmit,
                enabled = state.canSave,
                loading = state.isSaving,
            )
            Spacer(Modifier.height(AppSpacing.sm))
            AppOutlinedButton(
                text = "Save draft",
                onClick = viewModel::save,
                enabled = state.canSave,
            )
        } else {
            AppButton(
                text = if (viewModel.isEditMode) "Save changes" else "Save draft",
                onClick = viewModel::save,
                enabled = state.canSave,
                loading = state.isSaving,
            )
        }
    }
}
