package com.mitclass.hrleave.feature.admin.teams

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.admin.GenericCrudListScreen
import com.mitclass.hrleave.core.admin.SuperuserGate

@Composable
fun TeamsAdminScreen(isSuperuser: Boolean, viewModel: TeamsAdminViewModel = hiltViewModel()) {
    SuperuserGate(isSuperuser) {
        GenericCrudListScreen(engine = viewModel.engine)
    }
}
