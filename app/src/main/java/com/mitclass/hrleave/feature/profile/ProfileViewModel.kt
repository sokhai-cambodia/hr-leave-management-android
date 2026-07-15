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

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private fun initialState(): ProfileUiState {
        val user = authRepository.currentUser.value
        return ProfileUiState(fullName = user?.fullName.orEmpty(), email = user?.email.orEmpty())
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, savedMessage = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, savedMessage = null)
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null, savedMessage = null)
            when (
                val result = authRepository.updateProfile(
                    fullName = state.fullName.trim().ifBlank { null },
                    email = state.email.trim().ifBlank { null },
                )
            ) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    fullName = result.data.fullName.orEmpty(),
                    email = result.data.email,
                    savedMessage = "Profile updated",
                )
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = result.message)
            }
        }
    }
}
