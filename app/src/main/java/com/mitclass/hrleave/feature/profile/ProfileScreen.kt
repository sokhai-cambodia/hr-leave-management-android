package com.mitclass.hrleave.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.navigation.Destination
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner
import com.mitclass.hrleave.data.remote.dto.UserDto

private data class AdminEntry(val label: String, val icon: ImageVector, val destination: Destination)

private val adminEntries = listOf(
    AdminEntry("Policies", Icons.Outlined.Gavel, Destination.AdminPolicies),
    AdminEntry("Public Holidays", Icons.Outlined.Event, Destination.AdminPublicHolidays),
    AdminEntry("Leave Types", Icons.Outlined.Category, Destination.AdminLeaveTypes),
    AdminEntry("Teams", Icons.Outlined.Groups, Destination.AdminTeams),
    AdminEntry("Leave Balances", Icons.Outlined.AccountBalance, Destination.AdminLeaveBalances),
    AdminEntry("Admin Users", Icons.Outlined.AdminPanelSettings, Destination.AdminUsers),
)

private fun initials(fullName: String?, email: String): String {
    val source = fullName?.takeIf { it.isNotBlank() } ?: email
    val parts = source.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "?"
    }
}

private fun roleLabel(isSuperuser: Boolean, isApprover: Boolean): String = when {
    isSuperuser -> "Superuser"
    isApprover -> "Team owner / approver"
    else -> "Employee"
}

@Composable
fun ProfileScreen(
    user: UserDto,
    isApprover: Boolean = false,
    onChangePasswordClick: () -> Unit,
    onAdminEntryClick: (Destination) -> Unit = {},
    onBusinessCardClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) isEditing = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.fullName ?: user.email, style = MaterialTheme.typography.titleLarge)
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { isEditing = !isEditing }) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit profile")
            }
        }

        if (isEditing) {
            Spacer(Modifier.height(AppSpacing.md))
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
            Spacer(Modifier.height(AppSpacing.md))
            AppButton(
                text = "Save",
                onClick = viewModel::save,
                enabled = !state.isSaving && state.fullName.isNotBlank() && state.email.isNotBlank(),
                loading = state.isSaving,
            )
        }

        Spacer(Modifier.height(AppSpacing.lg))
        InfoRow(label = "Team", value = user.team?.name ?: "No team assigned")
        InfoRow(label = "Role", value = roleLabel(user.isSuperuser, isApprover))

        Spacer(Modifier.height(AppSpacing.lg))
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBusinessCardClick)
                .padding(vertical = AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Outlined.QrCode2, contentDescription = null)
            Text(
                text = "My Business Card",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = AppSpacing.md),
            )
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChangePasswordClick)
                .padding(vertical = AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = AppSpacing.md),
            )
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
        }
        HorizontalDivider()

        Spacer(Modifier.height(AppSpacing.lg))
        AppOutlinedButton(
            text = "Log out",
            onClick = onLogout,
            icon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
        )

        if (user.isSuperuser) {
            Spacer(Modifier.height(AppSpacing.xl))
            Text(text = "Admin", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(AppSpacing.sm))
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
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AdminRow(entry: AdminEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.md),
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
        Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
    }
}
