package com.mitclass.hrleave.feature.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.TeamsRepository
import com.mitclass.hrleave.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersAdminViewModel @Inject constructor(
    usersRepository: UsersRepository,
    teamsRepository: TeamsRepository,
) : ViewModel() {
    val engine = CrudEngine(viewModelScope, UserCrudAdapter(usersRepository, teamsRepository))
}
