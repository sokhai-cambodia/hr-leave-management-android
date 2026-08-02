package com.mitclass.hrleave.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import com.mitclass.hrleave.R
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes
import com.mitclass.hrleave.feature.profile.ProfileRoutes

/** Title shown in the shared top app bar for the current back-stack entry (Task 13.2). */
@Composable
fun screenTitle(entry: NavBackStackEntry?): String {
    val route = entry?.destination?.route ?: return stringResource(R.string.app_name)
    return when (route) {
        Destination.Dashboard.route -> stringResource(R.string.nav_tab_home)
        Destination.Leaves.route -> stringResource(R.string.nav_tab_leaves)
        Destination.Schedule.route -> stringResource(R.string.nav_tab_calendar)
        Destination.Profile.route -> stringResource(R.string.nav_tab_profile)
        Destination.BusinessCard.route -> stringResource(R.string.business_card_title)
        Destination.Settings.route -> stringResource(R.string.nav_title_settings)
        Destination.Approvals.route -> stringResource(R.string.nav_title_approvals)
        Destination.Notifications.route -> stringResource(R.string.nav_title_notifications)
        Destination.Recommendations.route -> stringResource(R.string.nav_title_recommendations)
        Destination.Reports.route -> stringResource(R.string.reports_nav_label)
        Destination.AdminPolicies.route -> stringResource(R.string.admin_entry_policies)
        Destination.AdminPublicHolidays.route -> stringResource(R.string.admin_entry_public_holidays)
        Destination.AdminLeaveTypes.route -> stringResource(R.string.admin_entry_leave_types)
        Destination.AdminTeams.route -> stringResource(R.string.admin_entry_teams)
        Destination.AdminLeaveBalances.route -> stringResource(R.string.admin_entry_leave_balances)
        Destination.AdminUsers.route -> stringResource(R.string.admin_entry_admin_users)
        ProfileRoutes.CHANGE_PASSWORD_ROUTE -> stringResource(R.string.change_password_nav_label)
        LeaveRequestRoutes.DETAIL_ROUTE -> stringResource(R.string.nav_title_request_details)
        LeaveRequestRoutes.FORM_ROUTE ->
            if (entry.arguments?.getString(LeaveRequestRoutes.FORM_ARG).isNullOrBlank()) {
                stringResource(R.string.nav_title_new_leave_request)
            } else {
                stringResource(R.string.nav_title_edit_leave_request)
            }
        LeavePlanRequestRoutes.DETAIL_ROUTE -> stringResource(R.string.nav_title_plan_details)
        LeavePlanRequestRoutes.FORM_ROUTE ->
            if (entry.arguments?.getString(LeavePlanRequestRoutes.FORM_ARG).isNullOrBlank()) {
                stringResource(R.string.nav_title_new_leave_plan_request)
            } else {
                stringResource(R.string.nav_title_edit_leave_plan_request)
            }
        else -> stringResource(R.string.app_name)
    }
}

/** Whether [route] is one of the 4 persistent bottom tabs (vs. a back-arrow pushed screen). */
fun isTopLevelRoute(route: String?): Boolean = BottomTab.entries.any { it.matchRoute == route }

/**
 * Create/edit forms get a close "X" instead of a back arrow (Task 13.6); every other
 * back-arrow pushed screen (detail/list/admin/notifications/approvals) keeps the arrow.
 */
fun isModalFormRoute(route: String?): Boolean = route in setOf(
    LeaveRequestRoutes.FORM_ROUTE,
    LeavePlanRequestRoutes.FORM_ROUTE,
    ProfileRoutes.CHANGE_PASSWORD_ROUTE,
)
