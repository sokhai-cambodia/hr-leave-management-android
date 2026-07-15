package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeaveBalancesResponseDto
import com.mitclass.hrleave.data.remote.dto.MessageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LeaveBalancesApi {
    /** Server pins this to the current calendar year — no client-controlled year filter exists. */
    @GET("leave-balances/me")
    suspend fun getMyBalances(): LeaveBalancesResponseDto

    @GET("leave-balances/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): LeaveBalancesResponseDto

    @POST("leave-balances/")
    suspend fun create(@Body body: LeaveBalanceUpsertDto): LeaveBalanceDto

    @PUT("leave-balances/{id}")
    suspend fun update(@Path("id") id: String, @Body body: LeaveBalanceUpsertDto): LeaveBalanceDto

    @DELETE("leave-balances/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
