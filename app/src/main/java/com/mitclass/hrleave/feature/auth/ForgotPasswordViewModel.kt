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

sealed interface ForgotPasswordUiState {
    data object Idle : ForgotPasswordUiState
    data object Loading : ForgotPasswordUiState
    data class Success(val message: String) : ForgotPasswordUiState
    data class Error(val message: String) : ForgotPasswordUiState
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun submit(email: String) {
        if (_uiState.value is ForgotPasswordUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            _uiState.value = when (val result = authRepository.recoverPassword(email)) {
                is AppResult.Success -> ForgotPasswordUiState.Success(result.data.message)
                is AppResult.Failure -> ForgotPasswordUiState.Error(result.message)
            }
        }
    }
}
