package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveBalancesResponseDto
import retrofit2.http.GET

interface LeaveBalancesApi {
    /** Server pins this to the current calendar year — no client-controlled year filter exists. */
    @GET("leave-balances/me")
    suspend fun getMyBalances(): LeaveBalancesResponseDto
}
