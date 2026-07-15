package com.mitclass.hrleave.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_PASSWORD_LENGTH = 8

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
) {
    val validationError: String?
        get() = when {
            newPassword.isNotEmpty() && newPassword.length < MIN_PASSWORD_LENGTH ->
                "New password must be at least $MIN_PASSWORD_LENGTH characters"
            confirmPassword.isNotEmpty() && newPassword != confirmPassword -> "Passwords don't match"
            else -> null
        }

    val canSubmit: Boolean
        get() = !isSaving &&
            currentPassword.isNotBlank() &&
            newPassword.length >= MIN_PASSWORD_LENGTH &&
            newPassword == confirmPassword
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPassword = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            when (val result = authRepository.updatePassword(state.currentPassword, state.newPassword)) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, success = true)
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = result.message)
            }
        }
    }
}
