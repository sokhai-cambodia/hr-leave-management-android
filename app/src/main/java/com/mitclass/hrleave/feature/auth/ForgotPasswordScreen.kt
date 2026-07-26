package com.mitclass.hrleave.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onHaveResetToken: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    val isLoading = uiState is ForgotPasswordUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.forgot_password_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.common_label_email),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        when (val state = uiState) {
            is ForgotPasswordUiState.Success -> Text(
                text = state.message,
                color = SuccessColor,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
            is ForgotPasswordUiState.Error -> ErrorBanner(
                message = state.message,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
            else -> Unit
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = stringResource(R.string.forgot_password_send_button),
            onClick = { viewModel.submit(email.trim()) },
            enabled = !isLoading && email.isNotBlank(),
            loading = isLoading,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        TextButton(onClick = onHaveResetToken) {
            Text(stringResource(R.string.forgot_password_have_token))
        }
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.auth_back_to_login))
        }
    }
}
