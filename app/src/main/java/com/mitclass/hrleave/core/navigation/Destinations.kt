package com.mitclass.hrleave.core.navigation

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Welcome : Destination("welcome")
    data object ForgotPassword : Destination("forgot_password")
    data object ResetPassword : Destination("reset_password")
}
