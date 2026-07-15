package com.mitclass.hrleave.feature.auth

import androidx.lifecycle.ViewModel
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Shared session state holder — the canonical place other features read `currentUser` from
 * for role/owner-id checks, and the single place `logout()` is triggered from the shell.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val currentUser: StateFlow<UserDto?> = authRepository.currentUser

    fun logout() {
        authRepository.logout()
    }
}
