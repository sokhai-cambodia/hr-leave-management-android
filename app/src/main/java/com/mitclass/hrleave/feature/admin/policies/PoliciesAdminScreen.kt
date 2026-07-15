package com.mitclass.hrleave.feature.admin.policies

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.admin.GenericCrudListScreen
import com.mitclass.hrleave.core.admin.SuperuserGate

@Composable
fun PoliciesAdminScreen(isSuperuser: Boolean, viewModel: PoliciesAdminViewModel = hiltViewModel()) {
    SuperuserGate(isSuperuser) {
        GenericCrudListScreen(engine = viewModel.engine)
    }
}
