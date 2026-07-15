package com.mitclass.hrleave.feature.leaveplanrequests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeavePlanDetailUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
import com.mitclass.hrleave.data.repository.LeavePlanRequestsRepository
import com.mitclass.hrleave.data.repository.LeaveTypesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class LeavePlanRequestFormUiState(
    val isLoading: Boolean = true,
    val leaveTypes: List<LeaveTypeDto> = emptyList(),
    val selectedLeaveTypeId: String? = null,
    val dates: List<LocalDate> = emptyList(),
    val description: String = "",
    val duplicateDateMessage: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
) {
    val canSave: Boolean
        get() = !isSaving && selectedLeaveTypeId != null && dates.isNotEmpty()
}

@HiltViewModel
class LeavePlanRequestFormViewModel @Inject constructor(
    private val leavePlanRequestsRepository: LeavePlanRequestsRepository,
    private val leaveTypesRepository: LeaveTypesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val existingId: String? = savedStateHandle[LeavePlanRequestRoutes.FORM_ARG]
    val isEditMode: Boolean get() = existingId != null

    private val _uiState = MutableStateFlow(LeavePlanRequestFormUiState())
    val uiState: StateFlow<LeavePlanRequestFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val leaveTypes = when (val result = leaveTypesRepository.listAllowPlan()) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> emptyList()
            }
            val id = existingId
            if (id == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, leaveTypes = leaveTypes)
                return@launch
            }
            when (val result = leavePlanRequestsRepository.get(id)) {
                is AppResult.Success -> {
                    val request = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        leaveTypes = leaveTypes,
                        selectedLeaveTypeId = request.leaveTypeId,
                        dates = request.details.map { LocalDate.parse(it.leaveDate) }.sorted(),
                        description = request.description.orEmpty(),
                    )
                }
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    leaveTypes = leaveTypes,
                    errorMessage = result.message,
                )
            }
        }
    }

    fun onLeaveTypeSelected(id: String) {
        _uiState.value = _uiState.value.copy(selectedLeaveTypeId = id)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun addDate(date: LocalDate) {
        val current = _uiState.value
        val updated = addDateIfNotDuplicate(current.dates, date)
        _uiState.value = if (updated == null) {
            current.copy(duplicateDateMessage = "That date is already in the list.")
        } else {
            current.copy(dates = updated, duplicateDateMessage = null)
        }
    }

    fun removeDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(dates = _uiState.value.dates - date)
    }

    fun dismissDuplicateDateMessage() {
        _uiState.value = _uiState.value.copy(duplicateDateMessage = null)
    }

    fun save() {
        val state = _uiState.value
        val leaveTypeId = state.selectedLeaveTypeId ?: return
        if (state.dates.isEmpty() || state.isSaving) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            val body = LeavePlanRequestUpsertDto(
                description = state.description.trim().ifBlank { null },
                leaveTypeId = leaveTypeId,
                details = state.dates.map { LeavePlanDetailUpsertDto(leaveDate = it.toString()) },
            )
            val result = existingId?.let { leavePlanRequestsRepository.update(it, body) }
                ?: leavePlanRequestsRepository.create(body)
            _uiState.value = when (result) {
                is AppResult.Success -> _uiState.value.copy(isSaving = false, saved = true)
                is AppResult.Failure -> _uiState.value.copy(isSaving = false, errorMessage = result.message)
            }
        }
    }
}
