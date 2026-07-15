package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestsResponseDto
import com.mitclass.hrleave.data.remote.dto.MessageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LeaveRequestsApi {
    @GET("leave-requests/")
    suspend fun list(
        @Query("owner_id") ownerId: String? = null,
        @Query("status") status: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): LeaveRequestsResponseDto

    @GET("leave-requests/{id}")
    suspend fun get(@Path("id") id: String): LeaveRequestDto

    @POST("leave-requests/")
    suspend fun create(@Body body: LeaveRequestUpsertDto): LeaveRequestDto

    @PUT("leave-requests/{id}")
    suspend fun update(@Path("id") id: String, @Body body: LeaveRequestUpsertDto): LeaveRequestDto

    @DELETE("leave-requests/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto

    @PUT("leave-requests/{id}/submit")
    suspend fun submit(@Path("id") id: String): LeaveRequestDto
}
