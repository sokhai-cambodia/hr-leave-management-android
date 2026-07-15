package com.mitclass.hrleave.core.network

import com.mitclass.hrleave.core.storage.TokenStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Test

class UnauthorizedInterceptorTest {

    private fun response(code: Int, request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message("")
        .build()

    private fun chainReturning(code: Int): Interceptor.Chain {
        val request = Request.Builder().url("http://localhost/api/v1/leave-requests/").build()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(request) } returns response(code, request)
        return chain
    }

    @Test
    fun `401 response clears the stored token and notifies the event bus`() {
        val tokenStore = mockk<TokenStore>(relaxed = true)
        every { tokenStore.getToken() } returns "stale-token"
        val authEventBus = mockk<AuthEventBus>(relaxed = true)
        val interceptor = UnauthorizedInterceptor(tokenStore, authEventBus)

        interceptor.intercept(chainReturning(401))

        verify { tokenStore.clearToken() }
        verify { authEventBus.notifyForcedLogout() }
    }

    @Test
    fun `non-401 response leaves the token and event bus untouched`() {
        val tokenStore = mockk<TokenStore>(relaxed = true)
        every { tokenStore.getToken() } returns "valid-token"
        val authEventBus = mockk<AuthEventBus>(relaxed = true)
        val interceptor = UnauthorizedInterceptor(tokenStore, authEventBus)

        interceptor.intercept(chainReturning(200))

        verify(exactly = 0) { tokenStore.clearToken() }
        verify(exactly = 0) { authEventBus.notifyForcedLogout() }
    }

    @Test
    fun `401 with no stored token does not re-fire the event bus`() {
        val tokenStore = mockk<TokenStore>(relaxed = true)
        every { tokenStore.getToken() } returns null
        val authEventBus = mockk<AuthEventBus>(relaxed = true)
        val interceptor = UnauthorizedInterceptor(tokenStore, authEventBus)

        interceptor.intercept(chainReturning(401))

        verify(exactly = 0) { authEventBus.notifyForcedLogout() }
    }
}
