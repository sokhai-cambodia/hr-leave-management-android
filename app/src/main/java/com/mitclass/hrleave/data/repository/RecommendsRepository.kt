package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.RecommendsApi
import com.mitclass.hrleave.data.remote.dto.LeaveRecommendationsResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendsRepository @Inject constructor(
    private val recommendsApi: RecommendsApi,
) {
    suspend fun recommendLeavePlan(year: Int): AppResult<LeaveRecommendationsResponseDto> =
        safeApiCall { recommendsApi.recommendLeavePlan(year) }
}
