package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.feature.dashboard.DashboardScreen
import com.mitclass.hrleave.feature.dashboard.QuickAction
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestDetailScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestFormScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestsListScreen

/**
 * Dashboard quick-action tiles — grows one entry per phase as each screen lands (Task 3.1's
 * comment on avoiding permanently-fake placeholders); Profile and Approvals stay drawer-only,
 * per SPEC §8, so they're deliberately absent here.
 */
private val dashboardQuickActions = listOf(
    QuickAction(label = "Leave Requests", icon = Icons.Filled.FactCheck, route = Destination.LeaveRequests.route),
)

/** Routes reachable from the authenticated shell's drawer (Task 2.1). */
@Composable
fun AuthenticatedNavHost(
    navController: NavHostController,
    user: UserDto,
    isApprover: Boolean = false,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = Destination.Dashboard.route, modifier = modifier) {
        composable(Destination.Dashboard.route) {
            DashboardScreen(
                user = user,
                isApprover = isApprover,
                quickActions = dashboardQuickActions,
                onQuickActionClick = { action -> navController.navigate(action.route) },
                onPendingApprovalsClick = { navController.navigate(Destination.Approvals.route) },
            )
        }
        composable(Destination.Schedule.route) { ComingSoonScreen("Schedule") }
        composable(Destination.LeavePlanRequests.route) { ComingSoonScreen("Leave Plan Requests") }
        composable(Destination.LeaveRequests.route) {
            LeaveRequestsListScreen(
                onItemClick = { id -> navController.navigate(LeaveRequestRoutes.detail(id)) },
                onCreateClick = { navController.navigate(LeaveRequestRoutes.FORM_CREATE_ROUTE) },
            )
        }
        composable(
            route = LeaveRequestRoutes.DETAIL_ROUTE,
            arguments = listOf(navArgument(LeaveRequestRoutes.DETAIL_ARG) { type = NavType.StringType }),
        ) {
            LeaveRequestDetailScreen(
                onEdit = { id -> navController.navigate(LeaveRequestRoutes.formEdit(id)) },
                onDeleted = { navController.popBackStack() },
            )
        }
        composable(
            route = LeaveRequestRoutes.FORM_ROUTE,
            arguments = listOf(
                navArgument(LeaveRequestRoutes.FORM_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            LeaveRequestFormScreen(onSaved = { navController.popBackStack() })
        }
        composable(Destination.Recommendations.route) { ComingSoonScreen("Recommendations") }
        composable(Destination.Approvals.route) { ComingSoonScreen("Approvals") }
        composable(Destination.Notifications.route) { ComingSoonScreen("Notifications") }
        composable(Destination.Profile.route) { ComingSoonScreen("Profile") }
        composable(Destination.AdminPolicies.route) { ComingSoonScreen("Policies") }
        composable(Destination.AdminLeaveTypes.route) { ComingSoonScreen("Leave Types") }
        composable(Destination.AdminTeams.route) { ComingSoonScreen("Teams") }
        composable(Destination.AdminLeaveBalances.route) { ComingSoonScreen("Leave Balances") }
        composable(Destination.AdminUsers.route) { ComingSoonScreen("Admin Users") }
    }
}
