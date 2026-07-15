package com.mitclass.hrleave.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.AuthEventBus
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.repository.AuthRepository
import com.mitclass.hrleave.data.repository.TeamsRepository
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
 *
 * Also owns the team-owner ("approver") detection heuristic: there's no `is_team_owner` flag
 * on `User`, so this fetches `GET /teams` once per session and checks whether any team's
 * `team_owner.id` matches the current user — not re-run per screen.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus,
    private val teamsRepository: TeamsRepository,
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _isApprover = MutableStateFlow(false)
    val isApprover: StateFlow<Boolean> = _isApprover.asStateFlow()

    private var teamOwnerCheckDone = false

    init {
        viewModelScope.launch {
            bootstrap()
            // Only start reacting to subsequent login/logout changes once the initial
            // bootstrap has resolved, so a stale null doesn't flash Unauthenticated first.
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _sessionState.value = SessionState.Authenticated(user)
                    if (!teamOwnerCheckDone) {
                        teamOwnerCheckDone = true
                        refreshIsApprover(user.id)
                    }
                } else {
                    _sessionState.value = SessionState.Unauthenticated
                    teamOwnerCheckDone = false
                    _isApprover.value = false
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

    private suspend fun refreshIsApprover(userId: String) {
        _isApprover.value = when (val result = teamsRepository.listTeams()) {
            is AppResult.Success -> result.data.any { it.teamOwner?.id == userId }
            is AppResult.Failure -> false
        }
    }

    fun logout() {
        authRepository.logout()
        _sessionState.value = SessionState.Unauthenticated
    }
}
