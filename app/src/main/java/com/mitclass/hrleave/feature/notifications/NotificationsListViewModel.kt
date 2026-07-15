package com.mitclass.hrleave.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.NotificationDto
import com.mitclass.hrleave.data.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

sealed interface NotificationsListUiState {
    data object Loading : NotificationsListUiState
    data class Loaded(
        val notifications: List<NotificationDto>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : NotificationsListUiState
    data class Error(val message: String) : NotificationsListUiState
}

@HiltViewModel
class NotificationsListViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NotificationsListUiState>(NotificationsListUiState.Loading)
    val uiState: StateFlow<NotificationsListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NotificationsListUiState.Loading
            _uiState.value = when (val result = notificationsRepository.fetchNotifications(skip = 0, limit = PAGE_SIZE)) {
                is AppResult.Success -> NotificationsListUiState.Loaded(
                    notifications = result.data.first,
                    canLoadMore = result.data.first.size < result.data.second,
                )
                is AppResult.Failure -> NotificationsListUiState.Error(result.message)
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value as? NotificationsListUiState.Loaded ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        viewModelScope.launch {
            _uiState.value = current.copy(isLoadingMore = true)
            when (val result = notificationsRepository.fetchNotifications(skip = current.notifications.size, limit = PAGE_SIZE)) {
                is AppResult.Success -> {
                    val combined = current.notifications + result.data.first
                    _uiState.value = NotificationsListUiState.Loaded(
                        notifications = combined,
                        canLoadMore = combined.size < result.data.second,
                    )
                }
                is AppResult.Failure -> _uiState.value = current.copy(isLoadingMore = false)
            }
        }
    }

    /** Marks read locally (optimistic) and on the server; navigation is the caller's responsibility. */
    fun markRead(id: String) {
        val current = _uiState.value as? NotificationsListUiState.Loaded ?: return
        val target = current.notifications.firstOrNull { it.id == id } ?: return
        if (target.isRead) return
        _uiState.value = current.copy(
            notifications = current.notifications.map { if (it.id == id) it.copy(isRead = true) else it },
        )
        viewModelScope.launch { notificationsRepository.markRead(id) }
    }

    fun markAllRead() {
        val current = _uiState.value as? NotificationsListUiState.Loaded ?: return
        _uiState.value = current.copy(notifications = current.notifications.map { it.copy(isRead = true) })
        viewModelScope.launch { notificationsRepository.markAllRead() }
    }
}
