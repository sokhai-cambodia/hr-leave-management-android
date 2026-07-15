package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.TeamsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TeamsApi {
    @GET("teams/")
    suspend fun listTeams(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): TeamsResponseDto
}
