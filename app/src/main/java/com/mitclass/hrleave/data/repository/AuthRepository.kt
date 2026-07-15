package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.storage.TokenStore
import com.mitclass.hrleave.data.remote.api.AuthApi
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.dto.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns login/logout and the cached current user for the whole app session — read by nearly
 * every other feature for role (`isSuperuser`) and owner-id (`currentUser.id`) checks.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) {
    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    fun hasToken(): Boolean = tokenStore.getToken() != null

    suspend fun login(identifier: String, password: String): AppResult<UserDto> {
        val tokenResult = safeApiCall { authApi.login(identifier, password) }
        if (tokenResult is AppResult.Failure) return tokenResult
        val token = (tokenResult as AppResult.Success).data
        tokenStore.setToken(token.accessToken)
        val meResult = fetchMe()
        if (meResult is AppResult.Failure) {
            tokenStore.clearToken()
        }
        return meResult
    }

    suspend fun fetchMe(): AppResult<UserDto> {
        val result = safeApiCall { authApi.getMe() }
        if (result is AppResult.Success) _currentUser.value = result.data
        return result
    }

    /** Session-restore path (Task 1.2): validate the stored token against the backend. */
    suspend fun restoreSession(): AppResult<UserDto> {
        val result = safeApiCall { authApi.testToken() }
        if (result is AppResult.Success) _currentUser.value = result.data
        return result
    }

    fun logout() {
        tokenStore.clearToken()
        _currentUser.value = null
    }
}
