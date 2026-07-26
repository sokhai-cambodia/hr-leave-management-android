package com.mitclass.hrleave.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Re-runs [onResume] every time this screen resumes after the first — e.g. after popping back
 * from a create/edit form. Skips the very first `ON_RESUME`: in Navigation-Compose, a
 * destination's [LocalLifecycleOwner] is its `NavBackStackEntry`, and `Lifecycle.addObserver`
 * synchronously replays state transitions up to the current state for a newly added observer. If
 * the entry is already reaching `RESUMED` when this effect attaches (i.e. on first composition),
 * that replay fires a synthetic `ON_RESUME` immediately — duplicating whatever initial load the
 * ViewModel already did in its `init {}`. Ignoring that first event keeps one load on first entry
 * and one refresh per genuine return, instead of two loads on first entry.
 */
@Composable
fun OnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)
    DisposableEffect(lifecycleOwner) {
        var isFirstResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (isFirstResume) {
                    isFirstResume = false
                } else {
                    currentOnResume()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
