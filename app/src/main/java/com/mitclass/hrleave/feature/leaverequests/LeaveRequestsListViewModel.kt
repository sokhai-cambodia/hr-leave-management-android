package com.mitclass.hrleave.feature.leaverequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.repository.AuthRepository
import com.mitclass.hrleave.data.repository.LeaveRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

sealed interface LeaveRequestsListUiState {
    data object Loading : LeaveRequestsListUiState
    data class Loaded(
        val requests: List<LeaveRequestDto>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : LeaveRequestsListUiState
    data class Error(val message: String) : LeaveRequestsListUiState
}

/** Owner-scoped from the start — always the caller's own requests, even for a superuser. */
@HiltViewModel
class LeaveRequestsListViewModel @Inject constructor(
    private val leaveRequestsRepository: LeaveRequestsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LeaveRequestsListUiState>(LeaveRequestsListUiState.Loading)
    val uiState: StateFlow<LeaveRequestsListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(false)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    init {
        load()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortDirection() {
        _sortAscending.value = !_sortAscending.value
    }

    fun onStatusFilterSelected(status: String?) {
        _statusFilter.value = status
    }

    /** Client-side filter + sort over currently-loaded pages — the list endpoint has no server-side search/sort. */
    fun visibleRequests(loaded: List<LeaveRequestDto>): List<LeaveRequestDto> {
        val query = _searchQuery.value.trim()
        val status = _statusFilter.value
        val filtered = loaded
            .filter { status == null || it.status == status }
            .filter {
                query.isBlank() ||
                    it.leaveType.name.contains(query, ignoreCase = true) ||
                    it.description.orEmpty().contains(query, ignoreCase = true)
            }
        val sorted = filtered.sortedBy { it.startDate }
        return if (_sortAscending.value) sorted else sorted.asReversed()
    }

    fun load() {
        val ownerId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = LeaveRequestsListUiState.Loading
            _uiState.value = when (val result = leaveRequestsRepository.listMine(ownerId, skip = 0, limit = PAGE_SIZE)) {
                is AppResult.Success -> LeaveRequestsListUiState.Loaded(
                    requests = result.data.first,
                    canLoadMore = result.data.first.size < result.data.second,
                )
                is AppResult.Failure -> LeaveRequestsListUiState.Error(result.message)
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value as? LeaveRequestsListUiState.Loaded ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        val ownerId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = current.copy(isLoadingMore = true)
            when (
                val result = leaveRequestsRepository.listMine(ownerId, skip = current.requests.size, limit = PAGE_SIZE)
            ) {
                is AppResult.Success -> {
                    val combined = current.requests + result.data.first
                    _uiState.value = LeaveRequestsListUiState.Loaded(
                        requests = combined,
                        canLoadMore = combined.size < result.data.second,
                    )
                }
                is AppResult.Failure -> _uiState.value = current.copy(isLoadingMore = false)
            }
        }
    }
}
