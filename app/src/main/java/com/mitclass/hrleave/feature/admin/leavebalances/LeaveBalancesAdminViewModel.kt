package com.mitclass.hrleave.feature.admin.leavebalances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.LeaveBalancesRepository
import com.mitclass.hrleave.data.repository.LeaveTypesRepository
import com.mitclass.hrleave.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaveBalancesAdminViewModel @Inject constructor(
    leaveBalancesRepository: LeaveBalancesRepository,
    usersRepository: UsersRepository,
    leaveTypesRepository: LeaveTypesRepository,
) : ViewModel() {
    val engine = CrudEngine(
        viewModelScope,
        LeaveBalanceCrudAdapter(leaveBalancesRepository, usersRepository, leaveTypesRepository),
    )
}
