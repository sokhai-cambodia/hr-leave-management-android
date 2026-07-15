package com.mitclass.hrleave.feature.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveRecommendationDto
import com.mitclass.hrleave.data.repository.RecommendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface RecommendationsUiState {
    data object Loading : RecommendationsUiState
    data class Loaded(val leaveTypeId: String, val items: List<LeaveRecommendationDto>) : RecommendationsUiState
    data class Error(val message: String) : RecommendationsUiState
}

/** Fetch & display only (Task 6.1) — selection lands in Task 6.2. */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendsRepository: RecommendsRepository,
) : ViewModel() {
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Loading)
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = RecommendationsUiState.Loading
            // Server already returns entries in chronological (day_of_year) order — pass
            // `data` straight through, no client re-sort that could corrupt that order.
            _uiState.value = when (val result = recommendsRepository.recommendLeavePlan(_selectedYear.value)) {
                is AppResult.Success -> RecommendationsUiState.Loaded(result.data.leaveTypeId, result.data.data)
                is AppResult.Failure -> RecommendationsUiState.Error(result.message)
            }
        }
    }

    fun onYearSelected(year: Int) {
        _selectedYear.value = year
        load()
    }
}
