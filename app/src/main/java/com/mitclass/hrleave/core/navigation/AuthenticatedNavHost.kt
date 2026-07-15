package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.feature.approvals.ApprovalsQueueScreen
import com.mitclass.hrleave.feature.dashboard.DashboardScreen
import com.mitclass.hrleave.feature.dashboard.QuickAction
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestDetailScreen
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestFormScreen
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestRoutes
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestsListScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestDetailScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestFormScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestsListScreen
import com.mitclass.hrleave.feature.recommendations.RecommendationsScreen
import com.mitclass.hrleave.feature.schedule.ScheduleScreen

/**
 * Dashboard quick-action tiles — grows one entry per phase as each screen lands (Task 3.1's
 * comment on avoiding permanently-fake placeholders); Profile and Approvals stay drawer-only,
 * per SPEC §8, so they're deliberately absent here.
 */
private val dashboardQuickActions = listOf(
    QuickAction(label = "Schedule", icon = Icons.Filled.CalendarMonth, route = Destination.Schedule.route),
    QuickAction(label = "Leave Requests", icon = Icons.Filled.FactCheck, route = Destination.LeaveRequests.route),
    QuickAction(
        label = "Leave Plan Requests",
        icon = Icons.Filled.EventNote,
        route = Destination.LeavePlanRequests.route,
    ),
    QuickAction(
        label = "Recommendations",
        icon = Icons.Filled.AutoAwesome,
        route = Destination.Recommendations.route,
    ),
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
        composable(Destination.Schedule.route) { ScheduleScreen() }
        composable(Destination.LeavePlanRequests.route) {
            LeavePlanRequestsListScreen(
                onItemClick = { id -> navController.navigate(LeavePlanRequestRoutes.detail(id)) },
                onCreateClick = { navController.navigate(LeavePlanRequestRoutes.FORM_CREATE_ROUTE) },
            )
        }
        composable(
            route = LeavePlanRequestRoutes.DETAIL_ROUTE,
            arguments = listOf(navArgument(LeavePlanRequestRoutes.DETAIL_ARG) { type = NavType.StringType }),
        ) {
            LeavePlanRequestDetailScreen(
                onEdit = { id -> navController.navigate(LeavePlanRequestRoutes.formEdit(id)) },
                onDeleted = { navController.popBackStack() },
            )
        }
        composable(
            route = LeavePlanRequestRoutes.FORM_ROUTE,
            arguments = listOf(
                navArgument(LeavePlanRequestRoutes.FORM_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(LeavePlanRequestRoutes.PREFILL_LEAVE_TYPE_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(LeavePlanRequestRoutes.PREFILL_DATES_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            LeavePlanRequestFormScreen(
                onSaved = { navController.popBackStack() },
                onSubmittedSuccess = { id ->
                    navController.navigate(LeavePlanRequestRoutes.detail(id)) {
                        popUpTo(LeavePlanRequestRoutes.FORM_ROUTE) { inclusive = true }
                    }
                },
            )
        }
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
        composable(Destination.Recommendations.route) {
            RecommendationsScreen(
                onUseSelectedDates = { leaveTypeId, dates ->
                    navController.navigate(LeavePlanRequestRoutes.formPrefill(leaveTypeId, dates))
                },
            )
        }
        composable(Destination.Approvals.route) { ApprovalsQueueScreen() }
        composable(Destination.Notifications.route) { ComingSoonScreen("Notifications") }
        composable(Destination.Profile.route) { ComingSoonScreen("Profile") }
        composable(Destination.AdminPolicies.route) { ComingSoonScreen("Policies") }
        composable(Destination.AdminLeaveTypes.route) { ComingSoonScreen("Leave Types") }
        composable(Destination.AdminTeams.route) { ComingSoonScreen("Teams") }
        composable(Destination.AdminLeaveBalances.route) { ComingSoonScreen("Leave Balances") }
        composable(Destination.AdminUsers.route) { ComingSoonScreen("Admin Users") }
    }
}
