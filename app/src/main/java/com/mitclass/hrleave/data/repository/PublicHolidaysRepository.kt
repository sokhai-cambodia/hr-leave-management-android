package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.PublicHolidaysApi
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.PublicHolidayUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicHolidaysRepository @Inject constructor(
    private val publicHolidaysApi: PublicHolidaysApi,
) {
    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<PublicHolidayDto>, Int>> =
        when (val result = safeApiCall { publicHolidaysApi.list(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: PublicHolidayUpsertDto): AppResult<PublicHolidayDto> =
        safeApiCall { publicHolidaysApi.create(body) }

    suspend fun update(id: String, body: PublicHolidayUpsertDto): AppResult<PublicHolidayDto> =
        safeApiCall { publicHolidaysApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { publicHolidaysApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
