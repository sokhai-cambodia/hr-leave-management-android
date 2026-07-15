package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveTypesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LeaveTypesApi {
    @GET("leave-types/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): LeaveTypesResponseDto
}
