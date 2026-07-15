package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.ScheduleDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("schedule/")
    suspend fun getSchedule(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): ScheduleDto
}
