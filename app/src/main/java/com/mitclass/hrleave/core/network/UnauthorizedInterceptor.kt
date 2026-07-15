package com.mitclass.hrleave.core.network

import com.mitclass.hrleave.core.storage.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Any 401 from any authenticated request means the stored token is dead: clear it and
 * notify [AuthEventBus] so the shell can force a re-login. The [AtomicBoolean] guard
 * collapses concurrent in-flight requests that all 401 at once into a single event.
 */
@Singleton
class UnauthorizedInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val authEventBus: AuthEventBus,
) : Interceptor {
    private val handlingLogout = AtomicBoolean(false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401 && tokenStore.getToken() != null) {
            if (handlingLogout.compareAndSet(false, true)) {
                tokenStore.clearToken()
                authEventBus.notifyForcedLogout()
                handlingLogout.set(false)
            }
        }
        return response
    }
}
