package com.mitclass.hrleave.feature.admin.leavebalances

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.admin.GenericCrudListScreen
import com.mitclass.hrleave.core.admin.SuperuserGate

@Composable
fun LeaveBalancesAdminScreen(isSuperuser: Boolean, viewModel: LeaveBalancesAdminViewModel = hiltViewModel()) {
    SuperuserGate(isSuperuser) {
        GenericCrudListScreen(engine = viewModel.engine)
    }
}
