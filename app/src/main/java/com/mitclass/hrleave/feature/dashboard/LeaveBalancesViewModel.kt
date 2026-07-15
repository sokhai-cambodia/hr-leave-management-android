package com.mitclass.hrleave.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.repository.LeaveBalancesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LeaveBalancesUiState {
    data object Loading : LeaveBalancesUiState
    data class Loaded(val balances: List<LeaveBalanceDto>) : LeaveBalancesUiState
    data class Error(val message: String) : LeaveBalancesUiState
}

@HiltViewModel
class LeaveBalancesViewModel @Inject constructor(
    private val leaveBalancesRepository: LeaveBalancesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LeaveBalancesUiState>(LeaveBalancesUiState.Loading)
    val uiState: StateFlow<LeaveBalancesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = LeaveBalancesUiState.Loading
            _uiState.value = when (val result = leaveBalancesRepository.getMyBalances()) {
                is AppResult.Success -> LeaveBalancesUiState.Loaded(result.data)
                is AppResult.Failure -> LeaveBalancesUiState.Error(result.message)
            }
        }
    }
}
