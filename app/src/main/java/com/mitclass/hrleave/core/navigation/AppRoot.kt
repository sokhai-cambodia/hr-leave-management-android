package com.mitclass.hrleave.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.feature.auth.AuthViewModel
import com.mitclass.hrleave.feature.auth.SessionState

/**
 * Root composable: a loading splash until app-start bootstrap (Task 1.2) resolves, then
 * either the unauthenticated flow or the authenticated shell — each owns its own NavHost,
 * so a login/logout/forced-logout transition simply swaps the whole subtree rather than
 * needing manual back-stack surgery.
 */
@Composable
fun AppRoot(authViewModel: AuthViewModel = hiltViewModel()) {
    when (val state = authViewModel.sessionState.collectAsState().value) {
        is SessionState.Loading -> LoadingSplash()
        is SessionState.Unauthenticated -> AuthNavHost()
        is SessionState.Authenticated -> AuthenticatedShell(
            user = state.user,
            isApprover = authViewModel.isApprover.collectAsState().value,
            unreadNotificationCount = authViewModel.unreadNotificationCount.collectAsState().value,
            onLogout = { authViewModel.logout() },
        )
    }
}

@Composable
private fun LoadingSplash() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
