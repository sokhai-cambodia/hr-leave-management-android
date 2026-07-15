package com.mitclass.hrleave.feature.admin.holidays

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.admin.GenericCrudListScreen
import com.mitclass.hrleave.core.admin.SuperuserGate

@Composable
fun PublicHolidaysAdminScreen(isSuperuser: Boolean, viewModel: PublicHolidaysAdminViewModel = hiltViewModel()) {
    SuperuserGate(isSuperuser) {
        GenericCrudListScreen(engine = viewModel.engine)
    }
}
