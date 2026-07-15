package com.mitclass.hrleave.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.mitclass.hrleave.feature.auth.AuthViewModel
import com.mitclass.hrleave.feature.auth.SessionState

/**
 * Root composable: shows a loading splash until app-start bootstrap (Task 1.2) resolves,
 * then hosts the nav graph with a start destination decided by that result — never
 * hardcoded to Login — and force-navigates (clearing the back stack) on every subsequent
 * login/logout/forced-logout transition.
 */
@Composable
fun AppRoot(authViewModel: AuthViewModel = hiltViewModel()) {
    when (val state = authViewModel.sessionState.collectAsState().value) {
        is SessionState.Loading -> LoadingSplash()
        is SessionState.Authenticated, is SessionState.Unauthenticated -> {
            val navController = rememberNavController()
            val startDestination = if (state is SessionState.Authenticated) {
                Destination.Welcome.route
            } else {
                Destination.Login.route
            }
            NavGraph(
                navController = navController,
                startDestination = startDestination,
                authViewModel = authViewModel,
            )

            val sessionState by authViewModel.sessionState.collectAsState()
            LaunchedEffect(sessionState) {
                val targetRoute = when (sessionState) {
                    is SessionState.Authenticated -> Destination.Welcome.route
                    is SessionState.Unauthenticated -> Destination.Login.route
                    is SessionState.Loading -> return@LaunchedEffect
                }
                if (navController.currentDestination?.route != targetRoute) {
                    navController.navigate(targetRoute) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingSplash() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
