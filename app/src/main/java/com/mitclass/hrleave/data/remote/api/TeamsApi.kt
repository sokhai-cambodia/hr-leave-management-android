package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.TeamDto
import com.mitclass.hrleave.data.remote.dto.TeamUpsertDto
import com.mitclass.hrleave.data.remote.dto.TeamsResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TeamsApi {
    @GET("teams/")
    suspend fun listTeams(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): TeamsResponseDto

    @POST("teams/")
    suspend fun create(@Body body: TeamUpsertDto): TeamDto

    @PUT("teams/{id}")
    suspend fun update(@Path("id") id: String, @Body body: TeamUpsertDto): TeamDto

    @DELETE("teams/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
