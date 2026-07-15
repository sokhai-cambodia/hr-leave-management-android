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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
            .padding(16.dp),
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.fullName,
            onValueChange = viewModel::onFullNameChange,
            label = { Text("Full name") },
            singleLine = true,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
        state.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }
        state.savedMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = viewModel::save,
            enabled = !state.isSaving && state.fullName.isNotBlank() && state.email.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Save")
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onChangePasswordClick, modifier = Modifier.fillMaxWidth()) {
            Text("Change password")
        }
        if (isSuperuser) {
            Spacer(Modifier.height(24.dp))
            Text(text = "Admin", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column {
                adminEntries.forEachIndexed { index, entry ->
                    AdminRow(entry = entry, onClick = { onAdminEntryClick(entry.destination) })
                    if (index != adminEntries.lastIndex) HorizontalDivider()
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
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = entry.icon, contentDescription = null)
        Text(
            text = entry.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
    }
}
