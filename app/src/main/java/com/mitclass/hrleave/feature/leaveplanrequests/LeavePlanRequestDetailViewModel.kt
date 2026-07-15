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

sealed interface PlanDeleteState {
    data object Idle : PlanDeleteState
    data object Deleting : PlanDeleteState
    data object Deleted : PlanDeleteState
    data class Error(val message: String) : PlanDeleteState
}

@HiltViewModel
class LeavePlanRequestDetailViewModel @Inject constructor(
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val requestId: String = checkNotNull(savedStateHandle[LeavePlanRequestRoutes.DETAIL_ARG])

    private val _uiState = MutableStateFlow<LeavePlanRequestDetailUiState>(LeavePlanRequestDetailUiState.Loading)
    val uiState: StateFlow<LeavePlanRequestDetailUiState> = _uiState.asStateFlow()

    private val _deleteState = MutableStateFlow<PlanDeleteState>(PlanDeleteState.Idle)
    val deleteState: StateFlow<PlanDeleteState> = _deleteState.asStateFlow()

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

    fun delete() {
        if (_deleteState.value is PlanDeleteState.Deleting) return
        viewModelScope.launch {
            _deleteState.value = PlanDeleteState.Deleting
            _deleteState.value = when (val result = leavePlanRequestsRepository.delete(requestId)) {
                is AppResult.Success -> PlanDeleteState.Deleted
                is AppResult.Failure -> PlanDeleteState.Error(result.message)
            }
        }
    }
}
