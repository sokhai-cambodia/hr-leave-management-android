package com.mitclass.hrleave.feature.leaverequests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
import com.mitclass.hrleave.data.repository.LeaveRequestsRepository
import com.mitclass.hrleave.data.repository.LeaveTypesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class LeaveRequestFormUiState(
    val isLoading: Boolean = true,
    val leaveTypes: List<LeaveTypeDto> = emptyList(),
    val selectedLeaveTypeId: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
    val submittedRequestId: String? = null,
) {
    val canSave: Boolean
        get() = !isSaving && selectedLeaveTypeId != null && startDate != null && endDate != null &&
            !endDate.isBefore(startDate)
}

@HiltViewModel
class LeaveRequestFormViewModel @Inject constructor(
    private val leaveRequestsRepository: LeaveRequestsRepository,
    private val leaveTypesRepository: LeaveTypesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val existingId: String? = savedStateHandle[LeaveRequestRoutes.FORM_ARG]
    val isEditMode: Boolean get() = existingId != null

    // Task 14.5: mirrors LeavePlanRequestFormViewModel's create-then-submit pattern — once
    // "Submit" (or a plain save) creates the row server-side, every subsequent save/submit
    // targets that row via update(), never a second create().
    private var createdId: String? = null
    private val effectiveId: String? get() = existingId ?: createdId

    private val _uiState = MutableStateFlow(LeaveRequestFormUiState())
    val uiState: StateFlow<LeaveRequestFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val leaveTypes = when (val result = leaveTypesRepository.listActive()) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> emptyList()
            }
            val id = existingId
            if (id == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, leaveTypes = leaveTypes)
                return@launch
            }
            when (val result = leaveRequestsRepository.get(id)) {
                is AppResult.Success -> {
                    val request = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        leaveTypes = leaveTypes,
                        selectedLeaveTypeId = request.leaveTypeId,
                        startDate = LocalDate.parse(request.startDate),
                        endDate = LocalDate.parse(request.endDate),
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

    fun onStartDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun onEndDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            when (val result = createOrUpdate(state)) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
                is AppResult.Failure -> _uiState.value =
                    _uiState.value.copy(isSaving = false, errorMessage = result.message)
            }
        }
    }

    /** "Submit": create/update, then immediately submit in the same gesture. */
    fun saveAndSubmit() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            when (val createResult = createOrUpdate(state)) {
                is AppResult.Failure -> _uiState.value =
                    _uiState.value.copy(isSaving = false, errorMessage = createResult.message)
                is AppResult.Success -> {
                    val id = createResult.data.id
                    createdId = id
                    _uiState.value = when (val submitResult = leaveRequestsRepository.submit(id)) {
                        is AppResult.Success -> _uiState.value.copy(isSaving = false, submittedRequestId = id)
                        is AppResult.Failure -> _uiState.value.copy(
                            isSaving = false,
                            errorMessage = "Draft saved, but couldn't submit: ${submitResult.message}",
                        )
                    }
                }
            }
        }
    }

    private suspend fun createOrUpdate(state: LeaveRequestFormUiState): AppResult<LeaveRequestDto> {
        val leaveTypeId = requireNotNull(state.selectedLeaveTypeId)
        val start = requireNotNull(state.startDate)
        val end = requireNotNull(state.endDate)
        val body = LeaveRequestUpsertDto(
            startDate = start.toString(),
            endDate = end.toString(),
            description = state.description.trim().ifBlank { null },
            leaveTypeId = leaveTypeId,
        )
        val id = effectiveId
        val result = if (id != null) leaveRequestsRepository.update(id, body) else leaveRequestsRepository.create(body)
        if (result is AppResult.Success && id == null) createdId = result.data.id
        return result
    }
}
