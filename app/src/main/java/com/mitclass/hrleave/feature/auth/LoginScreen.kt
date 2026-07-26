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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.ErrorBanner
import com.mitclass.hrleave.core.ui.TwoToneWordmark

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
        TwoToneWordmark()
        Spacer(Modifier.height(AppSpacing.lg))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(stringResource(R.string.login_headline_prefix)) }
                withStyle(SpanStyle(color = BrandPrimary, fontWeight = FontWeight.Bold)) { append(stringResource(R.string.login_headline_suffix)) }
            },
            fontSize = 28.sp,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        AppTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = stringResource(R.string.login_email_label),
            placeholder = stringResource(R.string.login_email_placeholder),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(AppSpacing.md))
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.login_password_label),
            placeholder = stringResource(R.string.login_password_placeholder),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
        Spacer(Modifier.height(AppSpacing.xs))
        TextButton(
            onClick = onForgotPassword,
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.login_forgot_password))
        }
        if (uiState is LoginUiState.Error) {
            ErrorBanner(
                message = (uiState as LoginUiState.Error).message,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
        }
        Spacer(Modifier.height(AppSpacing.lg))
        AppButton(
            text = stringResource(R.string.login_button),
            onClick = { viewModel.login(identifier.trim(), password) },
            enabled = !isLoading && identifier.isNotBlank() && password.isNotBlank(),
            loading = isLoading,
        )
    }
}
