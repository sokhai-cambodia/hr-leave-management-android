package com.mitclass.hrleave.core.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigates with `launchSingleTop` guarding against duplicate back-stack entries — without this,
 * a rapid double-tap (e.g. tapping again while the destination is still loading) pushes two
 * copies of the same screen, so a single back-press leaves the user looking at what appears to
 * be the same screen again instead of returning.
 */
fun NavHostController.navigateSingleTop(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route) {
        launchSingleTop = true
        builder()
    }
}
