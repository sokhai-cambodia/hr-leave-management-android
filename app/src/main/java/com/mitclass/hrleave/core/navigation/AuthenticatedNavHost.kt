package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
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
import com.mitclass.hrleave.feature.profile.BusinessCardScreen
import com.mitclass.hrleave.feature.profile.ChangePasswordScreen
import com.mitclass.hrleave.feature.profile.ProfileRoutes
import com.mitclass.hrleave.feature.profile.ProfileScreen
import com.mitclass.hrleave.feature.recommendations.RecommendationsScreen
import com.mitclass.hrleave.feature.reports.ReportsScreen
import com.mitclass.hrleave.feature.schedule.ScheduleScreen
import com.mitclass.hrleave.feature.settings.SettingsScreen

/**
 * Dashboard quick actions — Schedule/Leave Requests/Leave Plan Requests moved to bottom tabs
 * (Task 13.2/13.3), so only Recommendations and Reports remain as dashboard-only entry points.
 */
private val dashboardQuickActions = listOf(
    QuickAction(
        label = "Recommendations",
        icon = Icons.Outlined.AutoAwesome,
        route = Destination.Recommendations.route,
    ),
    QuickAction(
        label = "Reports",
        icon = Icons.Outlined.BarChart,
        route = Destination.Reports.route,
    ),
)

/** Routes reachable from the authenticated shell's bottom tabs (Task 13.2). */
@Composable
fun AuthenticatedNavHost(
    navController: NavHostController,
    user: UserDto,
    isApprover: Boolean = false,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Dashboard.route,
        modifier = modifier,
        enterTransition = NavEnterTransition,
        exitTransition = NavExitTransition,
        popEnterTransition = NavPopEnterTransition,
        popExitTransition = NavPopExitTransition,
    ) {
        composable(Destination.Dashboard.route) {
            DashboardScreen(
                user = user,
                isApprover = isApprover,
                quickActions = dashboardQuickActions,
                onQuickActionClick = { action -> navController.navigateSingleTop(action.route) },
                onPendingApprovalsClick = { navController.navigateSingleTop(Destination.Approvals.route) },
                onRequestLeaveClick = { navController.navigateSingleTop(LeaveRequestRoutes.FORM_CREATE_ROUTE) },
                onPlanLeaveClick = { navController.navigateSingleTop(LeavePlanRequestRoutes.FORM_CREATE_ROUTE) },
                onBusinessCardClick = { navController.navigateSingleTop(Destination.BusinessCard.route) },
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
                onRequestItemClick = { id -> navController.navigateSingleTop(LeaveRequestRoutes.detail(id)) },
                onPlanItemClick = { id -> navController.navigateSingleTop(LeavePlanRequestRoutes.detail(id)) },
            )
        }
        composable(
            route = LeavePlanRequestRoutes.DETAIL_ROUTE,
            arguments = listOf(navArgument(LeavePlanRequestRoutes.DETAIL_ARG) { type = NavType.StringType }),
        ) {
            LeavePlanRequestDetailScreen(
                onEdit = { id -> navController.navigateSingleTop(LeavePlanRequestRoutes.formEdit(id)) },
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
                    navController.navigateSingleTop(LeavePlanRequestRoutes.detail(id)) {
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
                onEdit = { id -> navController.navigateSingleTop(LeaveRequestRoutes.formEdit(id)) },
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
            LeaveRequestFormScreen(
                onSaved = { navController.popBackStack() },
                onSubmittedSuccess = { id ->
                    navController.navigateSingleTop(LeaveRequestRoutes.detail(id)) {
                        popUpTo(LeaveRequestRoutes.FORM_ROUTE) { inclusive = true }
                    }
                },
            )
        }
        composable(Destination.Recommendations.route) {
            RecommendationsScreen(
                onUseSelectedDates = { leaveTypeId, dates ->
                    navController.navigateSingleTop(LeavePlanRequestRoutes.formPrefill(leaveTypeId, dates))
                },
            )
        }
        composable(Destination.Reports.route) { ReportsScreen(isApprover = isApprover) }
        composable(Destination.Approvals.route) { ApprovalsQueueScreen() }
        composable(Destination.Notifications.route) {
            NotificationsListScreen(
                onNavigateToEntity = { entityType, entityId ->
                    val target = when (entityType) {
                        "leave_request" -> LeaveRequestRoutes.detail(entityId)
                        "leave_plan_request" -> LeavePlanRequestRoutes.detail(entityId)
                        else -> null
                    }
                    target?.let { navController.navigateSingleTop(it) }
                },
            )
        }
        composable(Destination.Profile.route) {
            ProfileScreen(
                user = user,
                isApprover = isApprover,
                onChangePasswordClick = { navController.navigateSingleTop(ProfileRoutes.CHANGE_PASSWORD_ROUTE) },
                onAdminEntryClick = { destination -> navController.navigateSingleTop(destination.route) },
                onBusinessCardClick = { navController.navigateSingleTop(Destination.BusinessCard.route) },
                onSettingsClick = { navController.navigateSingleTop(Destination.Settings.route) },
                onLogout = onLogout,
            )
        }
        composable(ProfileRoutes.CHANGE_PASSWORD_ROUTE) {
            ChangePasswordScreen(onSuccess = { navController.popBackStack() })
        }
        composable(Destination.BusinessCard.route) {
            BusinessCardScreen(user = user)
        }
        composable(Destination.Settings.route) { SettingsScreen() }
        composable(Destination.AdminPolicies.route) { PoliciesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminPublicHolidays.route) { PublicHolidaysAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminLeaveTypes.route) { LeaveTypesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminTeams.route) { TeamsAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminLeaveBalances.route) { LeaveBalancesAdminScreen(isSuperuser = user.isSuperuser) }
        composable(Destination.AdminUsers.route) { UsersAdminScreen(isSuperuser = user.isSuperuser) }
    }
}
