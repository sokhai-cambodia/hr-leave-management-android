package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.ScheduleApi
import com.mitclass.hrleave.data.remote.dto.ScheduleDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi,
) {
    /** Month-scoped server-side — a month change always triggers a fresh call, never a local re-filter of stale data. */
    suspend fun getSchedule(year: Int, month: Int): AppResult<ScheduleDto> =
        safeApiCall { scheduleApi.getSchedule(year, month) }
}
