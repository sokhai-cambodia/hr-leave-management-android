package com.mitclass.hrleave.feature.leaves

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mitclass.hrleave.core.navigation.Destination
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestsListScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestsListScreen

/**
 * The Leaves bottom tab: a TabRow switching between the existing Leave Requests / Leave Plan
 * Requests lists in place, reused unmodified — matches the Approvals queue's tab style for a
 * more native-Android feel than the pill SegmentedButton.
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
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Requests") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Plans") })
        }
        when (selectedTab) {
            0 -> LeaveRequestsListScreen(onItemClick = onRequestItemClick)
            else -> LeavePlanRequestsListScreen(onItemClick = onPlanItemClick)
        }
    }
}
