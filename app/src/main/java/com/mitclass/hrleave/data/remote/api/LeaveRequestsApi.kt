package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestsResponseDto
import retrofit2.http.GET
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
}
