package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val destination: Destination,
    val label: String,
    val icon: ImageVector,
)

private val baselineNavItems = listOf(
    NavItem(Destination.Dashboard, "Dashboard", Icons.Filled.Home),
    NavItem(Destination.Schedule, "Schedule", Icons.Filled.CalendarMonth),
    NavItem(Destination.LeavePlanRequests, "Leave Plan Requests", Icons.Filled.EventNote),
    NavItem(Destination.LeaveRequests, "Leave Requests", Icons.Filled.FactCheck),
    NavItem(Destination.Recommendations, "Recommendations", Icons.Filled.AutoAwesome),
    NavItem(Destination.Profile, "Profile", Icons.Filled.Person),
)

private val approverNavItem =
    NavItem(Destination.Approvals, "Approvals", Icons.Filled.Approval)

private val adminNavItems = listOf(
    NavItem(Destination.AdminPolicies, "Policies", Icons.Filled.Gavel),
    NavItem(Destination.AdminPublicHolidays, "Public Holidays", Icons.Filled.Event),
    NavItem(Destination.AdminLeaveTypes, "Leave Types", Icons.Filled.Category),
    NavItem(Destination.AdminTeams, "Teams", Icons.Filled.Groups),
    NavItem(Destination.AdminLeaveBalances, "Leave Balances", Icons.Filled.AccountBalance),
    NavItem(Destination.AdminUsers, "Admin Users", Icons.Filled.AdminPanelSettings),
)

/** Static role-based nav (Task 2.1). [isApprover] is wired in Task 2.2 — always `false` here. */
fun buildNavItems(isSuperuser: Boolean, isApprover: Boolean = false): List<NavItem> {
    val items = baselineNavItems.toMutableList()
    if (isApprover) {
        items.add(items.indexOfFirst { it.destination == Destination.Profile }, approverNavItem)
    }
    if (isSuperuser) items.addAll(adminNavItems)
    return items
}
