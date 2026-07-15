package com.mitclass.hrleave.feature.admin.holidays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.admin.CrudEngine
import com.mitclass.hrleave.data.repository.PublicHolidaysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PublicHolidaysAdminViewModel @Inject constructor(
    publicHolidaysRepository: PublicHolidaysRepository,
) : ViewModel() {
    val engine = CrudEngine(viewModelScope, PublicHolidayCrudAdapter(publicHolidaysRepository))
}
