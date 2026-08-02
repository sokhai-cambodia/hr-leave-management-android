package com.mitclass.hrleave.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.repository.AuthRepository
import com.mitclass.hrleave.data.repository.LeaveBalancesRepository
import com.mitclass.hrleave.data.repository.LeavePlanRequestsRepository
import com.mitclass.hrleave.data.repository.LeaveRequestsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val HISTORY_LIMIT = 200

data class LeaveTypeBalance(val leaveTypeName: String, val takenDays: Double, val availableDays: Double)

data class PersonalReport(
    val balances: List<LeaveTypeBalance>,
    val statusCounts: Map<String, Int>,
)

data class CurrentlyOutEntry(val name: String, val leaveTypeName: String, val returnsOn: String)

data class TeamReport(
    val totalCount: Int,
    val statusCounts: Map<String, Int>,
    val approvedDaysByLeaveType: Map<String, Double>,
    val currentlyOut: List<CurrentlyOutEntry>,
    val avgApprovalTurnaroundHours: Double?,
    val monthlyTrend: List<Pair<String, Int>>,
)

sealed interface ReportsUiState {
    data object Loading : ReportsUiState
    data class Loaded(val personal: PersonalReport, val team: TeamReport?) : ReportsUiState
    data class Error(val message: String) : ReportsUiState
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val leaveRequestsRepository: LeaveRequestsRepository,
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    private val leaveBalancesRepository: LeaveBalancesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReportsUiState>(ReportsUiState.Loading)
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    fun load(isApprover: Boolean) {
        val userId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            _uiState.value = ReportsUiState.Loading

            val balancesResult = leaveBalancesRepository.getMyBalances()
            val myRequestsResult = leaveRequestsRepository.listMine(userId, skip = 0, limit = HISTORY_LIMIT)
            val myPlanRequestsResult = leavePlanRequestsRepository.listMine(userId, skip = 0, limit = HISTORY_LIMIT)

            if (balancesResult !is AppResult.Success || myRequestsResult !is AppResult.Success || myPlanRequestsResult !is AppResult.Success) {
                val message = (balancesResult as? AppResult.Failure)?.message
                    ?: (myRequestsResult as? AppResult.Failure)?.message
                    ?: (myPlanRequestsResult as? AppResult.Failure)?.message.orEmpty()
                _uiState.value = ReportsUiState.Error(message)
                return@launch
            }

            val personal = buildPersonalReport(
                balances = balancesResult.data,
                requests = myRequestsResult.data.first,
                planRequests = myPlanRequestsResult.data.first,
            )

            val team = if (isApprover) {
                val teamRequestsResult = leaveRequestsRepository.listForApprover(userId, HISTORY_LIMIT)
                val teamPlanRequestsResult = leavePlanRequestsRepository.listForApprover(userId, HISTORY_LIMIT)
                if (teamRequestsResult is AppResult.Success && teamPlanRequestsResult is AppResult.Success) {
                    buildTeamReport(teamRequestsResult.data, teamPlanRequestsResult.data)
                } else {
                    null
                }
            } else {
                null
            }

            _uiState.value = ReportsUiState.Loaded(personal, team)
        }
    }
}
