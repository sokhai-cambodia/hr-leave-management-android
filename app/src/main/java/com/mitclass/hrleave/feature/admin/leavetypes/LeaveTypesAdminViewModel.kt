package com.mitclass.hrleave.feature.admin.leavetypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.LeaveTypesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaveTypesAdminViewModel @Inject constructor(
    leaveTypesRepository: LeaveTypesRepository,
) : ViewModel() {
    val engine = CrudEngine(viewModelScope, LeaveTypeCrudAdapter(leaveTypesRepository))
}
