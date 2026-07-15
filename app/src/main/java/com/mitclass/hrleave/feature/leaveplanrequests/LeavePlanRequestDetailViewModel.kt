package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.repository.LeavePlanRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LeavePlanRequestDetailUiState {
    data object Loading : LeavePlanRequestDetailUiState
    data class Loaded(val request: LeavePlanRequestDto) : LeavePlanRequestDetailUiState
    data class Error(val message: String) : LeavePlanRequestDetailUiState
}

@HiltViewModel
class LeavePlanRequestDetailViewModel @Inject constructor(
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val requestId: String = checkNotNull(savedStateHandle[LeavePlanRequestRoutes.DETAIL_ARG])

    private val _uiState = MutableStateFlow<LeavePlanRequestDetailUiState>(LeavePlanRequestDetailUiState.Loading)
    val uiState: StateFlow<LeavePlanRequestDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = LeavePlanRequestDetailUiState.Loading
            _uiState.value = when (val result = leavePlanRequestsRepository.get(requestId)) {
                is AppResult.Success -> LeavePlanRequestDetailUiState.Loaded(result.data)
                is AppResult.Failure -> LeavePlanRequestDetailUiState.Error(result.message)
            }
        }
    }
}
