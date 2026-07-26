package com.mitclass.hrleave.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.repository.PublicHolidaysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface UpcomingHolidayUiState {
    data object Loading : UpcomingHolidayUiState
    data class Loaded(val holiday: PublicHolidayDto?) : UpcomingHolidayUiState
    data object Error : UpcomingHolidayUiState
}

@HiltViewModel
class UpcomingHolidayViewModel @Inject constructor(
    private val publicHolidaysRepository: PublicHolidaysRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UpcomingHolidayUiState>(UpcomingHolidayUiState.Loading)
    val uiState: StateFlow<UpcomingHolidayUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch { refresh() }
    }

    /** Suspends until the fetch completes, so pull-to-refresh can await it before hiding its indicator. */
    suspend fun refresh() {
        if (_uiState.value !is UpcomingHolidayUiState.Loaded) {
            _uiState.value = UpcomingHolidayUiState.Loading
        }
        _uiState.value = when (val result = publicHolidaysRepository.listAll(0, 100)) {
            is AppResult.Success -> {
                val now = LocalDate.now()
                val next = result.data.first
                    .filter { LocalDate.parse(it.date) >= now }
                    .minByOrNull { it.date }
                UpcomingHolidayUiState.Loaded(next)
            }
            is AppResult.Failure -> UpcomingHolidayUiState.Error
        }
    }
}
