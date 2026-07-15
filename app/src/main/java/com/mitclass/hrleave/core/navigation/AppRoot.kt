package com.mitclass.hrleave.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.feature.auth.AuthViewModel
import com.mitclass.hrleave.feature.auth.LoginScreen
import com.mitclass.hrleave.feature.auth.WelcomeScreen

/**
 * Temporary session-state switch for Task 1.1 — replaced by a real Navigation-Compose
 * graph with a proper start-destination decision in Task 1.2 / Task 2.1's shell.
 */
@Composable
fun AppRoot(authViewModel: AuthViewModel = hiltViewModel()) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val user = currentUser
    if (user == null) {
        LoginScreen()
    } else {
        WelcomeScreen(user = user, onLogout = { authViewModel.logout() })
    }
}
