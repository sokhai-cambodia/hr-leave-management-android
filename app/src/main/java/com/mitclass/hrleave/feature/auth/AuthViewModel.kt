package com.mitclass.hrleave.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.AuthEventBus
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionState {
    data object Loading : SessionState
    data class Authenticated(val user: UserDto) : SessionState
    data object Unauthenticated : SessionState
}

/**
 * Shared session state holder — the canonical place other features read the current user
 * from for role/owner-id checks, and where app-start bootstrap and forced-logout (any 401,
 * see [AuthEventBus]) are handled so the nav graph's start destination is never hardcoded.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus,
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            bootstrap()
            // Only start reacting to subsequent login/logout changes once the initial
            // bootstrap has resolved, so a stale null doesn't flash Unauthenticated first.
            authRepository.currentUser.collect { user ->
                _sessionState.value = if (user != null) {
                    SessionState.Authenticated(user)
                } else {
                    SessionState.Unauthenticated
                }
            }
        }
        viewModelScope.launch {
            authEventBus.forcedLogout.collect {
                authRepository.logout()
                _sessionState.value = SessionState.Unauthenticated
            }
        }
    }

    private suspend fun bootstrap() {
        if (!authRepository.hasToken()) {
            _sessionState.value = SessionState.Unauthenticated
            return
        }
        when (val result = authRepository.restoreSession()) {
            is AppResult.Success -> _sessionState.value = SessionState.Authenticated(result.data)
            is AppResult.Failure -> {
                authRepository.logout()
                _sessionState.value = SessionState.Unauthenticated
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _sessionState.value = SessionState.Unauthenticated
    }
}
