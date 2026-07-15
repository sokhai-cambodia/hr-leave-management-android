package com.mitclass.hrleave.feature.leaves

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mitclass.hrleave.core.navigation.Destination
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.PillTabRow
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestsListScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestsListScreen

/**
 * The Leaves bottom tab: a [PillTabRow] switching between the existing Leave Requests / Leave
 * Plan Requests lists in place, reused unmodified — matches STYLE_GUIDE.md's pill tab bar spec.
 */
@Composable
fun LeavesTabScreen(
    initialTab: String,
    onRequestItemClick: (String) -> Unit,
    onPlanItemClick: (String) -> Unit,
) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(if (initialTab == Destination.Leaves.PLANS_TAB) 1 else 0)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        PillTabRow(
            titles = listOf("Requests", "Plans"),
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(AppSpacing.lg),
        )
        when (selectedTab) {
            0 -> LeaveRequestsListScreen(onItemClick = onRequestItemClick)
            else -> LeavePlanRequestsListScreen(onItemClick = onPlanItemClick)
        }
    }
}
