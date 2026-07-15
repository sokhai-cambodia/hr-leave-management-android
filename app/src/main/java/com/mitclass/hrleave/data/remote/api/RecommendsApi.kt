package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.LeaveRecommendationsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendsApi {
    @GET("recommends/leave-plan")
    suspend fun recommendLeavePlan(@Query("year") year: Int): LeaveRecommendationsResponseDto
}
