package com.mitclass.hrleave.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The 4 persistent bottom tabs (Task 13.2), replacing the drawer's static role-based nav list.
 * Approvals/Notifications/Admin/Recommendations/detail+form screens are reached from within
 * these tabs (dashboard cards, Profile list, FAB sheet, row taps) as back-arrow pushed screens,
 * not as tabs of their own.
 */
enum class BottomTab(
    val matchRoute: String,
    val navRoute: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
) {
    HOME(Destination.Dashboard.route, Destination.Dashboard.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    LEAVES(
        Destination.Leaves.route,
        Destination.Leaves.route(),
        "Leaves",
        Icons.AutoMirrored.Filled.Assignment,
        Icons.AutoMirrored.Outlined.Assignment,
    ),
    CALENDAR(
        Destination.Schedule.route,
        Destination.Schedule.route,
        "Calendar",
        Icons.Filled.CalendarMonth,
        Icons.Outlined.CalendarMonth,
    ),
    PROFILE(Destination.Profile.route, Destination.Profile.route, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
}
