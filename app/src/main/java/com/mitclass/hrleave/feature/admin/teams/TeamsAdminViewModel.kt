package com.mitclass.hrleave.feature.admin.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.TeamsRepository
import com.mitclass.hrleave.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamsAdminViewModel @Inject constructor(
    teamsRepository: TeamsRepository,
    usersRepository: UsersRepository,
) : ViewModel() {
    val engine = CrudEngine(viewModelScope, TeamCrudAdapter(teamsRepository, usersRepository))
}
