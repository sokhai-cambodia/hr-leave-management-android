package com.mitclass.hrleave.feature.approvals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.repository.AuthRepository
import com.mitclass.hrleave.data.repository.LeavePlanRequestsRepository
import com.mitclass.hrleave.data.repository.LeaveRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ApprovalsQueueUiState {
    data object Loading : ApprovalsQueueUiState
    data class Loaded(
        val leaveRequests: List<LeaveRequestDto>,
        val leavePlanRequests: List<LeavePlanRequestDto>,
    ) : ApprovalsQueueUiState
    data class Error(val message: String) : ApprovalsQueueUiState
}

/** Approve/reject queue for the caller's team-owner role — server-side filtered to their assigned rows. */
@HiltViewModel
class ApprovalsQueueViewModel @Inject constructor(
    private val leaveRequestsRepository: LeaveRequestsRepository,
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApprovalsQueueUiState>(ApprovalsQueueUiState.Loading)
    val uiState: StateFlow<ApprovalsQueueUiState> = _uiState.asStateFlow()

    private val _processingIds = MutableStateFlow<Set<String>>(emptySet())
    val processingIds: StateFlow<Set<String>> = _processingIds.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    init {
        load()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortDirection() {
        _sortAscending.value = !_sortAscending.value
    }

    /** Client-side filter (by requester name/leave type) + sort by start date over the loaded queue. */
    fun visibleLeaveRequests(loaded: List<LeaveRequestDto>): List<LeaveRequestDto> {
        val query = _searchQuery.value.trim()
        val filtered = loaded.filter {
            query.isBlank() ||
                (it.owner.fullName ?: it.owner.email).contains(query, ignoreCase = true) ||
                it.leaveType.name.contains(query, ignoreCase = true)
        }
        val sorted = filtered.sortedBy { it.startDate }
        return if (_sortAscending.value) sorted else sorted.asReversed()
    }

    fun visibleLeavePlanRequests(loaded: List<LeavePlanRequestDto>): List<LeavePlanRequestDto> {
        val query = _searchQuery.value.trim()
        val filtered = loaded.filter {
            query.isBlank() ||
                (it.owner.fullName ?: it.owner.email).contains(query, ignoreCase = true) ||
                it.leaveType.name.contains(query, ignoreCase = true)
        }
        val sorted = filtered.sortedBy { request -> request.details.minOfOrNull { it.leaveDate }.orEmpty() }
        return if (_sortAscending.value) sorted else sorted.asReversed()
    }

    fun load() {
        val approverId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = ApprovalsQueueUiState.Loading
            _processingIds.value = emptySet()
            val leaveRequestsResult = leaveRequestsRepository.listPendingForApprover(approverId)
            val leavePlanRequestsResult = leavePlanRequestsRepository.listPendingForApprover(approverId)
            _uiState.value = if (
                leaveRequestsResult is AppResult.Success && leavePlanRequestsResult is AppResult.Success
            ) {
                ApprovalsQueueUiState.Loaded(leaveRequestsResult.data, leavePlanRequestsResult.data)
            } else {
                val message = (leaveRequestsResult as? AppResult.Failure)?.message
                    ?: (leavePlanRequestsResult as? AppResult.Failure)?.message.orEmpty()
                ApprovalsQueueUiState.Error(message)
            }
        }
    }

    fun approveLeaveRequest(id: String) = performAction(id) { leaveRequestsRepository.approve(id) }
    fun rejectLeaveRequest(id: String) = performAction(id) { leaveRequestsRepository.reject(id) }
    fun approveLeavePlanRequest(id: String) = performAction(id) { leavePlanRequestsRepository.approve(id) }
    fun rejectLeavePlanRequest(id: String) = performAction(id) { leavePlanRequestsRepository.reject(id) }

    fun dismissActionError() {
        _actionError.value = null
    }

    private fun performAction(id: String, action: suspend () -> AppResult<*>) {
        if (id in _processingIds.value) return
        viewModelScope.launch {
            _processingIds.value = _processingIds.value + id
            when (val result = action()) {
                is AppResult.Success -> load()
                is AppResult.Failure -> {
                    _actionError.value = result.message
                    _processingIds.value = _processingIds.value - id
                }
            }
        }
    }
}
