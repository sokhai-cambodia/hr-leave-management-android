package com.mitclass.hrleave.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session-wide signal for "the server just told us our token is no longer valid."
 * Emitted by [UnauthorizedInterceptor]; collected by the navigation layer (Task 1.2)
 * to force-navigate to Login and clear the back stack.
 */
@Singleton
class AuthEventBus @Inject constructor() {
    private val _forcedLogout = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val forcedLogout: SharedFlow<Unit> = _forcedLogout.asSharedFlow()

    fun notifyForcedLogout() {
        _forcedLogout.tryEmit(Unit)
    }
}
