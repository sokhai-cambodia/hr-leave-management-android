package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestsResponseDto
import com.mitclass.hrleave.data.remote.dto.MessageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LeavePlanRequestsApi {
    @GET("leave-plan-requests/")
    suspend fun list(
        @Query("owner_id") ownerId: String? = null,
        @Query("status") status: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): LeavePlanRequestsResponseDto

    @GET("leave-plan-requests/{id}")
    suspend fun get(@Path("id") id: String): LeavePlanRequestDto

    @POST("leave-plan-requests/")
    suspend fun create(@Body body: LeavePlanRequestUpsertDto): LeavePlanRequestDto

    @PUT("leave-plan-requests/{id}")
    suspend fun update(@Path("id") id: String, @Body body: LeavePlanRequestUpsertDto): LeavePlanRequestDto

    @DELETE("leave-plan-requests/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto

    @PUT("leave-plan-requests/{id}/submit")
    suspend fun submit(@Path("id") id: String): LeavePlanRequestDto
}
