package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.repository.AuthRepository
import com.mitclass.hrleave.data.repository.LeavePlanRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

sealed interface LeavePlanRequestsListUiState {
    data object Loading : LeavePlanRequestsListUiState
    data class Loaded(
        val requests: List<LeavePlanRequestDto>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : LeavePlanRequestsListUiState
    data class Error(val message: String) : LeavePlanRequestsListUiState
}

/** Owner-scoped from the start — mirrors LeaveRequestsListViewModel's pattern (Task 4.1). */
@HiltViewModel
class LeavePlanRequestsListViewModel @Inject constructor(
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LeavePlanRequestsListUiState>(LeavePlanRequestsListUiState.Loading)
    val uiState: StateFlow<LeavePlanRequestsListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val ownerId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = LeavePlanRequestsListUiState.Loading
            _uiState.value = when (
                val result = leavePlanRequestsRepository.listMine(ownerId, skip = 0, limit = PAGE_SIZE)
            ) {
                is AppResult.Success -> LeavePlanRequestsListUiState.Loaded(
                    requests = result.data.first,
                    canLoadMore = result.data.first.size < result.data.second,
                )
                is AppResult.Failure -> LeavePlanRequestsListUiState.Error(result.message)
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value as? LeavePlanRequestsListUiState.Loaded ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        val ownerId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = current.copy(isLoadingMore = true)
            when (
                val result = leavePlanRequestsRepository.listMine(ownerId, skip = current.requests.size, limit = PAGE_SIZE)
            ) {
                is AppResult.Success -> {
                    val combined = current.requests + result.data.first
                    _uiState.value = LeavePlanRequestsListUiState.Loaded(
                        requests = combined,
                        canLoadMore = combined.size < result.data.second,
                    )
                }
                is AppResult.Failure -> _uiState.value = current.copy(isLoadingMore = false)
            }
        }
    }
}
