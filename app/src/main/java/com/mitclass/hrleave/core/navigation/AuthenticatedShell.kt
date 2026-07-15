package com.mitclass.hrleave.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mitclass.hrleave.core.notifications.NotificationPermissionRequester
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.feature.leaveplanrequests.LeavePlanRequestRoutes
import com.mitclass.hrleave.feature.leaverequests.LeaveRequestRoutes

/**
 * Bottom-tab shell (Task 13.2), replacing the drawer. Approvals/Notifications/Admin/
 * Recommendations/detail+form screens are pushed as back-arrow secondary screens over the tab
 * content — the shared top bar shows a back arrow instead of nothing once off a tab route.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedShell(
    user: UserDto,
    isApprover: Boolean = false,
    unreadNotificationCount: Int = 0,
    onLogout: () -> Unit,
) {
    NotificationPermissionRequester()
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = isTopLevelRoute(currentRoute)
    var showCreateSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle(backStackEntry)) },
                // STYLE_GUIDE.md: "App bar: flat (elevation 0), background matches scaffold."
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
                navigationIcon = {
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            if (isModalFormRoute(currentRoute)) {
                                Icon(Icons.Outlined.Close, contentDescription = "Close")
                            } else {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                },
                actions = {
                    if (isTopLevel) {
                        NotificationBellChip(
                            unreadCount = unreadNotificationCount,
                            onClick = { navController.navigate(Destination.Notifications.route) },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (isTopLevel) {
                // Rounded-square (not circular) per the Flutter client's actual FAB shape.
                // ui/home.jpg docks the FAB so its bottom half overlaps the bottom bar, rather
                // than floating the full default gap above it — the offset closes that gap.
                FloatingActionButton(
                    onClick = { showCreateSheet = true },
                    modifier = Modifier.offset(y = 28.dp),
                    shape = RoundedCornerShape(20.dp),
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Create")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (isTopLevel) {
                // STYLE_GUIDE.md: "Bottom nav: surface-colored background ... elevation 8."
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                ) {
                    BottomTab.entries.take(2).forEach {
                        BottomTabButton(
                            tab = it,
                            currentRoute = currentRoute,
                            navController = navController,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    BottomTab.entries.drop(2).forEach {
                        BottomTabButton(
                            tab = it,
                            currentRoute = currentRoute,
                            navController = navController,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        AuthenticatedNavHost(
            navController = navController,
            user = user,
            isApprover = isApprover,
            onLogout = onLogout,
            modifier = Modifier.padding(paddingValues),
        )
    }

    if (showCreateSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState,
        ) {
            CreateActionSheetContent(
                onRequestLeave = {
                    showCreateSheet = false
                    navController.navigate(LeaveRequestRoutes.FORM_CREATE_ROUTE)
                },
                onPlanLeave = {
                    showCreateSheet = false
                    navController.navigate(LeavePlanRequestRoutes.FORM_CREATE_ROUTE)
                },
            )
        }
    }
}

@Composable
private fun BottomTabButton(
    tab: BottomTab,
    currentRoute: String?,
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier,
) {
    val selected = currentRoute == tab.matchRoute
    // STYLE_GUIDE.md: "primary color for selected icon/label, unselected at 45% opacity."
    val tint = if (selected) BrandPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    Column(
        modifier = modifier
            .clickable(onClick = {
                if (!selected) {
                    navController.navigate(tab.navRoute) {
                        popUpTo(Destination.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = if (selected) tab.filledIcon else tab.outlinedIcon, contentDescription = tab.label, tint = tint)
        Text(text = tab.label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}

@Composable
private fun NotificationBellChip(unreadCount: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(LightFieldFill)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge(containerColor = DangerColor) { Text(unreadCount.coerceAtMost(99).toString()) }
                }
            },
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
private fun CreateActionSheetContent(onRequestLeave: () -> Unit, onPlanLeave: () -> Unit) {
    Column(modifier = Modifier.padding(bottom = AppSpacing.lg)) {
        Text(
            text = "Create",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        )
        ListItem(
            headlineContent = { Text("Request Leave") },
            leadingContent = { Icon(Icons.AutoMirrored.Outlined.FactCheck, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onRequestLeave),
        )
        ListItem(
            headlineContent = { Text("Plan Leave") },
            leadingContent = { Icon(Icons.AutoMirrored.Outlined.EventNote, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPlanLeave),
        )
    }
}
