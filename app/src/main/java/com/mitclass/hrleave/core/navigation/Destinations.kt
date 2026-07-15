package com.mitclass.hrleave.core.navigation

sealed class Destination(val route: String) {
    // Unauthenticated flow
    data object Login : Destination("login")
    data object ForgotPassword : Destination("forgot_password")
    data object ResetPassword : Destination("reset_password")

    // Authenticated shell — baseline (every role)
    data object Dashboard : Destination("dashboard")
    data object Schedule : Destination("schedule")
    data object LeavePlanRequests : Destination("leave_plan_requests")
    data object LeaveRequests : Destination("leave_requests")
    data object Recommendations : Destination("recommendations")
    data object Approvals : Destination("approvals")
    data object Notifications : Destination("notifications")
    data object Profile : Destination("profile")

    // Authenticated shell — superuser only
    data object AdminPolicies : Destination("admin_policies")
    data object AdminPublicHolidays : Destination("admin_public_holidays")
    data object AdminLeaveTypes : Destination("admin_leave_types")
    data object AdminTeams : Destination("admin_teams")
    data object AdminLeaveBalances : Destination("admin_leave_balances")
    data object AdminUsers : Destination("admin_users")
}
