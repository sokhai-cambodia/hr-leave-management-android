package com.mitclass.hrleave.core.navigation

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Welcome : Destination("welcome")
}
