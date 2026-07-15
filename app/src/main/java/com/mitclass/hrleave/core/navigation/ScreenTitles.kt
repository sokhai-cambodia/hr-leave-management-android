package com.mitclass.hrleave.core.navigation

import androidx.navigation.NavBackStackEntry
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes
import com.mitclass.hrleave.feature.profile.ProfileRoutes

/** Title shown in the shared top app bar for the current back-stack entry (Task 13.2). */
fun screenTitle(entry: NavBackStackEntry?): String {
    val route = entry?.destination?.route ?: return "HR Leave"
    return when (route) {
        Destination.Dashboard.route -> "Home"
        Destination.Leaves.route -> "Leaves"
        Destination.Schedule.route -> "Calendar"
        Destination.Profile.route -> "Profile"
        Destination.Approvals.route -> "Approvals"
        Destination.Notifications.route -> "Notifications"
        Destination.Recommendations.route -> "Recommendations"
        Destination.AdminPolicies.route -> "Policies"
        Destination.AdminPublicHolidays.route -> "Public Holidays"
        Destination.AdminLeaveTypes.route -> "Leave Types"
        Destination.AdminTeams.route -> "Teams"
        Destination.AdminLeaveBalances.route -> "Leave Balances"
        Destination.AdminUsers.route -> "Admin Users"
        ProfileRoutes.CHANGE_PASSWORD_ROUTE -> "Change Password"
        LeaveRequestRoutes.DETAIL_ROUTE -> "Leave Request"
        LeaveRequestRoutes.FORM_ROUTE ->
            if (entry.arguments?.getString(LeaveRequestRoutes.FORM_ARG).isNullOrBlank()) "New Leave Request" else "Edit Leave Request"
        LeavePlanRequestRoutes.DETAIL_ROUTE -> "Leave Plan Request"
        LeavePlanRequestRoutes.FORM_ROUTE ->
            if (entry.arguments?.getString(LeavePlanRequestRoutes.FORM_ARG).isNullOrBlank()) {
                "New Leave Plan Request"
            } else {
                "Edit Leave Plan Request"
            }
        else -> "HR Leave"
    }
}

/** Whether [route] is one of the 4 persistent bottom tabs (vs. a back-arrow pushed screen). */
fun isTopLevelRoute(route: String?): Boolean = BottomTab.entries.any { it.matchRoute == route }
