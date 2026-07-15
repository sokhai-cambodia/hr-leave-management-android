package com.mitclass.hrleave.core.navigation

sealed class Destination(val route: String) {
    // Unauthenticated flow
    data object Login : Destination("login")
    data object ForgotPassword : Destination("forgot_password")
    data object ResetPassword : Destination("reset_password")

    // Authenticated shell — baseline (every role)
    data object Dashboard : Destination("dashboard")
    data object Schedule : Destination("schedule")

    /**
     * The Leaves bottom tab hosts a pill toggle between the Requests/Plans lists in place
     * (Task 13.2) rather than two separate nav destinations.
     */
    data object Leaves : Destination("leaves?tab={tab}") {
        const val TAB_ARG = "tab"
        const val REQUESTS_TAB = "requests"
        const val PLANS_TAB = "plans"
        fun route(tab: String = REQUESTS_TAB) = "leaves?tab=$tab"
    }
    data object Recommendations : Destination("recommendations")
    data object Approvals : Destination("approvals")
    data object Notifications : Destination("notifications")
    data object Profile : Destination("profile")
    data object BusinessCard : Destination("business_card")

    // Authenticated shell — superuser only
    data object AdminPolicies : Destination("admin_policies")
    data object AdminPublicHolidays : Destination("admin_public_holidays")
    data object AdminLeaveTypes : Destination("admin_leave_types")
    data object AdminTeams : Destination("admin_teams")
    data object AdminLeaveBalances : Destination("admin_leave_balances")
    data object AdminUsers : Destination("admin_users")
}
