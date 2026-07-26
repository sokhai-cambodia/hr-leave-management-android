package com.mitclass.hrleave.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
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

/**
 * Standard slide+fade transitions for [androidx.navigation.compose.NavHost]. Navigation-Compose's
 * own default (when these aren't set) is `EnterTransition.None`/`ExitTransition.None` — an
 * instant, unanimated cut between destinations that reads as a screen "popping" in rather than
 * transitioning smoothly.
 */
private const val NavTransitionDurationMs = 300

val NavEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(NavTransitionDurationMs),
    ) + fadeIn(animationSpec = tween(NavTransitionDurationMs))
}

val NavExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(NavTransitionDurationMs),
    ) + fadeOut(animationSpec = tween(NavTransitionDurationMs))
}

val NavPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(NavTransitionDurationMs),
    ) + fadeIn(animationSpec = tween(NavTransitionDurationMs))
}

val NavPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(NavTransitionDurationMs),
    ) + fadeOut(animationSpec = tween(NavTransitionDurationMs))
}
