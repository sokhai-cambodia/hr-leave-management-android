package com.mitclass.hrleave.feature.leaves

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mitclass.hrleave.core.navigation.Destination
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestsListScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestsListScreen

/**
 * The Leaves bottom tab (Task 13.2): a pill-shaped segmented control switching between the
 * existing Leave Requests / Leave Plan Requests lists in place, reused unmodified.
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
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = BrandPrimary,
                    activeContentColor = Color.White,
                    inactiveContainerColor = LightFieldFill,
                ),
                label = { Text("Requests") },
            )
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = BrandPrimary,
                    activeContentColor = Color.White,
                    inactiveContainerColor = LightFieldFill,
                ),
                label = { Text("Plans") },
            )
        }
        when (selectedTab) {
            0 -> LeaveRequestsListScreen(onItemClick = onRequestItemClick)
            else -> LeavePlanRequestsListScreen(onItemClick = onPlanItemClick)
        }
    }
}
