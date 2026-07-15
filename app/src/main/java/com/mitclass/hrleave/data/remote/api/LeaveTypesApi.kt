package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypeUpsertDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypesResponseDto
import com.mitclass.hrleave.data.remote.dto.MessageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LeaveTypesApi {
    @GET("leave-types/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): LeaveTypesResponseDto

    @POST("leave-types/")
    suspend fun create(@Body body: LeaveTypeUpsertDto): LeaveTypeDto

    @PUT("leave-types/{id}")
    suspend fun update(@Path("id") id: String, @Body body: LeaveTypeUpsertDto): LeaveTypeDto

    @DELETE("leave-types/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
