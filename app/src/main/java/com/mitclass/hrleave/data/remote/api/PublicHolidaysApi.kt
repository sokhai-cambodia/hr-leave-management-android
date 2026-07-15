package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.PublicHolidayUpsertDto
import com.mitclass.hrleave.data.remote.dto.PublicHolidaysResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicHolidaysApi {
    @GET("public-holidays/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): PublicHolidaysResponseDto

    @POST("public-holidays/")
    suspend fun create(@Body body: PublicHolidayUpsertDto): PublicHolidayDto

    @PUT("public-holidays/{id}")
    suspend fun update(@Path("id") id: String, @Body body: PublicHolidayUpsertDto): PublicHolidayDto

    @DELETE("public-holidays/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
