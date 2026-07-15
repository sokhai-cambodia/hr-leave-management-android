package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.PoliciesResponseDto
import com.mitclass.hrleave.data.remote.dto.PolicyDto
import com.mitclass.hrleave.data.remote.dto.PolicyUpsertDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PoliciesApi {
    @GET("policies/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): PoliciesResponseDto

    @POST("policies/")
    suspend fun create(@Body body: PolicyUpsertDto): PolicyDto

    @PUT("policies/{id}")
    suspend fun update(@Path("id") id: String, @Body body: PolicyUpsertDto): PolicyDto

    @DELETE("policies/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
