package com.mitclass.hrleave.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mitclass.hrleave.feature.auth.AuthViewModel
import com.mitclass.hrleave.feature.auth.ForgotPasswordScreen
import com.mitclass.hrleave.feature.auth.LoginScreen
import com.mitclass.hrleave.feature.auth.ResetPasswordScreen
import com.mitclass.hrleave.feature.auth.SessionState
import com.mitclass.hrleave.feature.auth.WelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destination.Login.route) {
            LoginScreen(
                onForgotPassword = { navController.navigate(Destination.ForgotPassword.route) },
            )
        }
        composable(Destination.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onHaveResetToken = { navController.navigate(Destination.ResetPassword.route) },
            )
        }
        composable(Destination.ResetPassword.route) {
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.popBackStack(Destination.Login.route, inclusive = false)
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Destination.Welcome.route) {
            val sessionState by authViewModel.sessionState.collectAsState()
            val user = (sessionState as? SessionState.Authenticated)?.user
            if (user != null) {
                WelcomeScreen(user = user, onLogout = { authViewModel.logout() })
            }
        }
    }
}
