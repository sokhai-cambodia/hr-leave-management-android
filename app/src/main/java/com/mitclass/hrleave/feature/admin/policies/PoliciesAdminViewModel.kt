package com.mitclass.hrleave.feature.admin.policies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.PoliciesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PoliciesAdminViewModel @Inject constructor(
    policiesRepository: PoliciesRepository,
) : ViewModel() {
    val engine = CrudEngine(viewModelScope, PolicyCrudAdapter(policiesRepository))
}
