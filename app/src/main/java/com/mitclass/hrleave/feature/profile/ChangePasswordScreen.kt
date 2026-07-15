package com.mitclass.hrleave.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner

@Composable
fun ChangePasswordScreen(
    onSuccess: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
    ) {
        Text(text = "Change password", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(AppSpacing.lg))
        PasswordField(
            label = "Current password",
            value = state.currentPassword,
            onValueChange = viewModel::onCurrentPasswordChange,
            enabled = !state.isSaving,
        )
        Spacer(Modifier.height(AppSpacing.md))
        PasswordField(
            label = "New password",
            value = state.newPassword,
            onValueChange = viewModel::onNewPasswordChange,
            enabled = !state.isSaving,
        )
        Spacer(Modifier.height(AppSpacing.md))
        PasswordField(
            label = "Confirm new password",
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            enabled = !state.isSaving,
        )
        state.validationError?.let {
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.md))
        }
        state.errorMessage?.let {
            ErrorBanner(message = it, modifier = Modifier.padding(top = AppSpacing.md))
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = "Update password",
            onClick = viewModel::submit,
            enabled = state.canSubmit,
            loading = state.isSaving,
        )
    }
}

@Composable
private fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    var visible by remember { mutableStateOf(false) }
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                )
            }
        },
    )
}
