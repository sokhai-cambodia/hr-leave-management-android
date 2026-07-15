package com.mitclass.hrleave.feature.leaverequests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.repository.LeaveRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LeaveRequestDetailUiState {
    data object Loading : LeaveRequestDetailUiState
    data class Loaded(val request: LeaveRequestDto) : LeaveRequestDetailUiState
    data class Error(val message: String) : LeaveRequestDetailUiState
}

@HiltViewModel
class LeaveRequestDetailViewModel @Inject constructor(
    private val leaveRequestsRepository: LeaveRequestsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val requestId: String = checkNotNull(savedStateHandle[LeaveRequestRoutes.DETAIL_ARG])

    private val _uiState = MutableStateFlow<LeaveRequestDetailUiState>(LeaveRequestDetailUiState.Loading)
    val uiState: StateFlow<LeaveRequestDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = LeaveRequestDetailUiState.Loading
            _uiState.value = when (val result = leaveRequestsRepository.get(requestId)) {
                is AppResult.Success -> LeaveRequestDetailUiState.Loaded(result.data)
                is AppResult.Failure -> LeaveRequestDetailUiState.Error(result.message)
            }
        }
    }
}
