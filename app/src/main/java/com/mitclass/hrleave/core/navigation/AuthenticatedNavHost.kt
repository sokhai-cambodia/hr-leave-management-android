package com.mitclass.hrleave.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.feature.dashboard.DashboardScreen

/** Routes reachable from the authenticated shell's drawer (Task 2.1). */
@Composable
fun AuthenticatedNavHost(
    navController: NavHostController,
    user: UserDto,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = Destination.Dashboard.route, modifier = modifier) {
        composable(Destination.Dashboard.route) {
            DashboardScreen(user = user)
        }
        composable(Destination.Schedule.route) { ComingSoonScreen("Schedule") }
        composable(Destination.LeavePlanRequests.route) { ComingSoonScreen("Leave Plan Requests") }
        composable(Destination.LeaveRequests.route) { ComingSoonScreen("Leave Requests") }
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
