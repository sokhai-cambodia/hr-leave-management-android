package com.mitclass.hrleave.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = uiState is LoginUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "HR Leave",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(32.dp))
        AppTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = "Email",
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(AppSpacing.md))
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    )
                }
            },
        )
        Spacer(Modifier.height(AppSpacing.xs))
        TextButton(onClick = onForgotPassword, enabled = !isLoading) {
            Text("Forgot password?")
        }
        if (uiState is LoginUiState.Error) {
            ErrorBanner(
                message = (uiState as LoginUiState.Error).message,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = "Log in",
            onClick = { viewModel.login(identifier.trim(), password) },
            enabled = !isLoading && identifier.isNotBlank() && password.isNotBlank(),
            loading = isLoading,
        )
    }
}
