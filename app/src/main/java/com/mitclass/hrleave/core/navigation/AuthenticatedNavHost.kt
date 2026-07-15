package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.feature.admin.holidays.PublicHolidaysAdminScreen
import com.mitclass.hrleave.feature.admin.leavebalances.LeaveBalancesAdminScreen
import com.mitclass.hrleave.feature.admin.leavetypes.LeaveTypesAdminScreen
import com.mitclass.hrleave.feature.admin.policies.PoliciesAdminScreen
import com.mitclass.hrleave.feature.admin.teams.TeamsAdminScreen
import com.mitclass.hrleave.feature.admin.users.UsersAdminScreen
import com.mitclass.hrleave.feature.approvals.ApprovalsQueueScreen
import com.mitclass.hrleave.feature.dashboard.DashboardScreen
import com.mitclass.hrleave.feature.dashboard.QuickAction
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestDetailScreen
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestFormScreen
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestDetailScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestFormScreen
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes
import com.mitclass.hrleave.feature.leaves.LeavesTabScreen
import com.mitclass.hrleave.feature.notifications.NotificationsListScreen
import com.mitclass.hrleave.feature.profile.ChangePasswordScreen
import com.mitclass.hrleave.feature.profile.ProfileRoutes
import com.mitclass.hrleave.feature.profile.ProfileScreen
import com.mitclass.hrleave.feature.recommendations.RecommendationsScreen
import com.mitclass.hrleave.feature.schedule.ScheduleScreen

/**
 * Dashboard quick actions — Schedule/Leave Requests/Leave Plan Requests moved to bottom tabs
 * (Task 13.2/13.3), so only Recommendations remains as a dashboard-only entry point.
 */
private val dashboardQuickActions = listOf(
    QuickAction(
        label = "Recommendations",
        icon = Icons.Filled.AutoAwesome,
        route = Destination.Recommendations.route,
    ),
)

/** Routes reachable from the authenticated shell's bottom tabs (Task 13.2). */
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
        composable(
            route = Destination.Leaves.route,
            arguments = listOf(
                navArgument(Destination.Leaves.TAB_ARG) {
                    type = NavType.StringType
                    defaultValue = Destination.Leaves.REQUESTS_TAB
                },
            ),
        ) { entry ->
            LeavesTabScreen(
                initialTab = entry.arguments?.getString(Destination.Leaves.TAB_ARG) ?: Destination.Leaves.REQUESTS_TAB,
                onRequestItemClick = { id -> navController.navigate(LeaveRequestRoutes.detail(id)) },
                onRequestCreateClick = { navController.navigate(LeaveRequestRoutes.FORM_CREATE_ROUTE) },
                onPlanItemClick = { id -> navController.navigate(LeavePlanRequestRoutes.detail(id)) },
                onPlanCreateClick = { navController.navigate(LeavePlanRequestRoutes.FORM_CREATE_ROUTE) },
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
        composable(Destination.Notifications.route) {
            NotificationsListScreen(
                onNavigateToEntity = { entityType ->
                    val target = when (entityType) {
                        "leave_request" -> Destination.Leaves.route(Destination.Leaves.REQUESTS_TAB)
                        "leave_plan_request" -> Destination.Leaves.route(Destination.Leaves.PLANS_TAB)
                        else -> null
                    }
                    target?.let { navController.navigate(it) }
                },
            )
        }
        composable(Destination.Profile.route) {
            ProfileScreen(
                onChangePasswordClick = { navController.navigate(ProfileRoutes.CHANGE_PASSWORD_ROUTE) },
                isSuperuser = user.isSuperuser,
                onAdminEntryClick = { destination -> navController.navigate(destination.route) },
            )
        }
        composable(ProfileRoutes.CHANGE_PASSWORD_ROUTE) {
            ChangePasswordScreen(onSuccess = { navController.popBackStack() })
        }
        composable(Destination.AdminPolicies.route) { PoliciesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminPublicHolidays.route) { PublicHolidaysAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminLeaveTypes.route) { LeaveTypesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminTeams.route) { TeamsAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminLeaveBalances.route) { LeaveBalancesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminUsers.route) { UsersAdminScreen(isSuperuser = user.isSuperuser) }
    }
}
