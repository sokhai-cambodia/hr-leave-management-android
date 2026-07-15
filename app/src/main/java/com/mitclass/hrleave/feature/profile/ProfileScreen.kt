package com.mitclass.hrleave.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.navigation.Destination
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner

private data class AdminEntry(val label: String, val icon: ImageVector, val destination: Destination)

private val adminEntries = listOf(
    AdminEntry("Policies", Icons.Filled.Gavel, Destination.AdminPolicies),
    AdminEntry("Public Holidays", Icons.Filled.Event, Destination.AdminPublicHolidays),
    AdminEntry("Leave Types", Icons.Filled.Category, Destination.AdminLeaveTypes),
    AdminEntry("Teams", Icons.Filled.Groups, Destination.AdminTeams),
    AdminEntry("Leave Balances", Icons.Filled.AccountBalance, Destination.AdminLeaveBalances),
    AdminEntry("Admin Users", Icons.Filled.AdminPanelSettings, Destination.AdminUsers),
)

@Composable
fun ProfileScreen(
    onChangePasswordClick: () -> Unit,
    isSuperuser: Boolean = false,
    onAdminEntryClick: (Destination) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg),
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(AppSpacing.lg))
        AppTextField(
            value = state.fullName,
            onValueChange = viewModel::onFullNameChange,
            label = "Full name",
            singleLine = true,
            enabled = !state.isSaving,
        )
        Spacer(Modifier.height(AppSpacing.md))
        AppTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            singleLine = true,
            enabled = !state.isSaving,
        )
        state.errorMessage?.let {
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.md))
        }
        state.savedMessage?.let {
            Text(text = it, color = SuccessColor, modifier = Modifier.padding(top = AppSpacing.md))
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = "Save",
            onClick = viewModel::save,
            enabled = !state.isSaving && state.fullName.isNotBlank() && state.email.isNotBlank(),
            loading = state.isSaving,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        AppOutlinedButton(text = "Change password", onClick = onChangePasswordClick)
        if (isSuperuser) {
            Spacer(Modifier.height(AppSpacing.xl))
            Text(text = "Admin", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(AppSpacing.sm))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CardCornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    adminEntries.forEachIndexed { index, entry ->
                        AdminRow(entry = entry, onClick = { onAdminEntryClick(entry.destination) })
                        if (index != adminEntries.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRow(entry: AdminEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = entry.icon, contentDescription = null)
        Text(
            text = entry.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = AppSpacing.md),
        )
        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
    }
}
