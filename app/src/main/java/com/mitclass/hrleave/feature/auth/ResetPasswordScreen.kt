package com.mitclass.hrleave.feature.auth

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner

private const val MIN_PASSWORD_LENGTH = 8

@Composable
fun ResetPasswordScreen(
    onResetSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = uiState is ResetPasswordUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is ResetPasswordUiState.Success) onResetSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.reset_password_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.reset_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))
        AppTextField(
            value = token,
            onValueChange = { token = it },
            label = stringResource(R.string.reset_password_token_label),
            enabled = !isLoading,
        )
        Spacer(Modifier.height(AppSpacing.md))
        AppTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = stringResource(R.string.password_new_label),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) {
                            stringResource(R.string.common_content_desc_hide_password)
                        } else {
                            stringResource(R.string.common_content_desc_show_password)
                        },
                    )
                }
            },
        )
        if (uiState is ResetPasswordUiState.Error) {
            ErrorBanner(
                message = (uiState as ResetPasswordUiState.Error).message,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = stringResource(R.string.reset_password_button),
            onClick = { viewModel.submit(token.trim(), newPassword) },
            enabled = !isLoading && token.isNotBlank() && newPassword.length >= MIN_PASSWORD_LENGTH,
            loading = isLoading,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.auth_back_to_login))
        }
    }
}
