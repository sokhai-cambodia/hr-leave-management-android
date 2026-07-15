package com.mitclass.hrleave.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.ScheduleTeamLeaveEntryDto
import com.mitclass.hrleave.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

sealed interface ScheduleUiState {
    data object Loading : ScheduleUiState
    data class Loaded(
        val holidays: List<PublicHolidayDto>,
        val teamLeave: List<ScheduleTeamLeaveEntryDto>,
    ) : ScheduleUiState
    data class Error(val message: String) : ScheduleUiState
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    private val _yearMonth = MutableStateFlow(YearMonth.now())
    val yearMonth: StateFlow<YearMonth> = _yearMonth.asStateFlow()

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val ym = _yearMonth.value
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            _uiState.value = when (val result = scheduleRepository.getSchedule(ym.year, ym.monthValue)) {
                is AppResult.Success -> ScheduleUiState.Loaded(result.data.publicHolidays, result.data.teamLeave)
                is AppResult.Failure -> ScheduleUiState.Error(result.message)
            }
        }
    }

    fun previousMonth() {
        _yearMonth.value = _yearMonth.value.minusMonths(1)
        load()
    }

    fun nextMonth() {
        _yearMonth.value = _yearMonth.value.plusMonths(1)
        load()
    }
}

fun PublicHolidayDto.holidayDate(): LocalDate = LocalDate.parse(date)
