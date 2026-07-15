package com.mitclass.hrleave.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mitclass.hrleave.feature.auth.ForgotPasswordScreen
import com.mitclass.hrleave.feature.auth.LoginScreen
import com.mitclass.hrleave.feature.auth.ResetPasswordScreen

/** Nav graph for the unauthenticated flow (Login / Forgot / Reset password), no shell chrome. */
@Composable
fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.Login.route) {
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
    }
}
