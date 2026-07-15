package com.mitclass.hrleave.core.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.errors.ApiError
import com.mitclass.hrleave.data.remote.api.HealthCheckApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/** Throwaway connectivity check for Task 0.2 — superseded by real auth screens in Phase 1. */
sealed interface SmokeCheckState {
    data object Loading : SmokeCheckState
    data class Healthy(val healthy: Boolean) : SmokeCheckState
    data class Error(val message: String) : SmokeCheckState
}

@HiltViewModel
class SmokeCheckViewModel @Inject constructor(
    private val healthCheckApi: HealthCheckApi,
) : ViewModel() {
    private val _state = MutableStateFlow<SmokeCheckState>(SmokeCheckState.Loading)
    val state: StateFlow<SmokeCheckState> = _state.asStateFlow()

    init {
        check()
    }

    fun check() {
        viewModelScope.launch {
            _state.value = SmokeCheckState.Loading
            _state.value = try {
                SmokeCheckState.Healthy(healthCheckApi.check())
            } catch (e: IOException) {
                SmokeCheckState.Error(ApiError.NETWORK_ERROR_MESSAGE)
            } catch (e: HttpException) {
                SmokeCheckState.Error(ApiError.parse(e.response()?.errorBody()?.string()))
            }
        }
    }
}
