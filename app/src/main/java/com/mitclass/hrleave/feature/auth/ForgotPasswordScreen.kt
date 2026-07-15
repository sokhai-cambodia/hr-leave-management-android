package com.mitclass.hrleave.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
        Text(text = "Reset your password", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Enter your account email and we'll send you a password reset link.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        when (val state = uiState) {
            is ForgotPasswordUiState.Success -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
            is ForgotPasswordUiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
            else -> Unit
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { viewModel.submit(email.trim()) },
            enabled = !isLoading && email.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Send reset email")
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onHaveResetToken) {
            Text("I already have a reset token")
        }
        TextButton(onClick = onBack) {
            Text("Back to login")
        }
    }
}
