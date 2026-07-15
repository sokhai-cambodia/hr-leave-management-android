package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeavePlanRequestsApi
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeavePlanRequestsRepository @Inject constructor(
    private val leavePlanRequestsApi: LeavePlanRequestsApi,
) {
    /** Own submissions only, regardless of role — mirrors LeaveRequestsRepository.listMine. */
    suspend fun listMine(ownerId: String, skip: Int, limit: Int): AppResult<Pair<List<LeavePlanRequestDto>, Int>> =
        when (val result = safeApiCall { leavePlanRequestsApi.list(ownerId = ownerId, skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun get(id: String): AppResult<LeavePlanRequestDto> = safeApiCall { leavePlanRequestsApi.get(id) }

    suspend fun create(body: LeavePlanRequestUpsertDto): AppResult<LeavePlanRequestDto> =
        safeApiCall { leavePlanRequestsApi.create(body) }

    /** Backend replaces the full date set on update — callers must send the complete current list. */
    suspend fun update(id: String, body: LeavePlanRequestUpsertDto): AppResult<LeavePlanRequestDto> =
        safeApiCall { leavePlanRequestsApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { leavePlanRequestsApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
