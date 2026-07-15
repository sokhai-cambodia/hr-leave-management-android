package com.mitclass.hrleave.feature.auth

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

sealed interface ResetPasswordUiState {
    data object Idle : ResetPasswordUiState
    data object Loading : ResetPasswordUiState
    data object Success : ResetPasswordUiState
    data class Error(val message: String) : ResetPasswordUiState
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.Idle)
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun submit(token: String, newPassword: String) {
        if (_uiState.value is ResetPasswordUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = ResetPasswordUiState.Loading
            _uiState.value = when (val result = authRepository.resetPassword(token, newPassword)) {
                is AppResult.Success -> ResetPasswordUiState.Success
                is AppResult.Failure -> ResetPasswordUiState.Error(result.message)
            }
        }
    }
}
