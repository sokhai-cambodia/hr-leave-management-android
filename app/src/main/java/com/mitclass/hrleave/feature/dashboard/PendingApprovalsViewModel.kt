package com.mitclass.hrleave.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.repository.ApprovalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PendingApprovalsUiState {
    data object Loading : PendingApprovalsUiState
    data class Loaded(val total: Int) : PendingApprovalsUiState
    data object Error : PendingApprovalsUiState
}

/** Backs the dashboard's tappable "Pending Approvals" card, shown only when `isApprover`. */
@HiltViewModel
class PendingApprovalsViewModel @Inject constructor(
    private val approvalsRepository: ApprovalsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PendingApprovalsUiState>(PendingApprovalsUiState.Loading)
    val uiState: StateFlow<PendingApprovalsUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            _uiState.value = when (val result = approvalsRepository.pendingCount()) {
                is AppResult.Success -> PendingApprovalsUiState.Loaded(result.data.total)
                is AppResult.Failure -> PendingApprovalsUiState.Error
            }
        }
    }
}
