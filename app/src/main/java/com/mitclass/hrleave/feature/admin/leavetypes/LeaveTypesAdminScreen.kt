package com.mitclass.hrleave.feature.admin.leavetypes

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.admin.GenericCrudListScreen
import com.mitclass.hrleave.core.admin.SuperuserGate

@Composable
fun LeaveTypesAdminScreen(isSuperuser: Boolean, viewModel: LeaveTypesAdminViewModel = hiltViewModel()) {
    SuperuserGate(isSuperuser) {
        GenericCrudListScreen(engine = viewModel.engine)
    }
}
