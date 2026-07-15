package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveTypesApi
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypeUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveTypesRepository @Inject constructor(
    private val leaveTypesApi: LeaveTypesApi,
) {
    suspend fun listActive(): AppResult<List<LeaveTypeDto>> =
        when (val result = safeApiCall { leaveTypesApi.list(limit = 100) }) {
            is AppResult.Success -> AppResult.Success(result.data.data.filter { it.isActive })
            is AppResult.Failure -> result
        }

    /** Leave Plan Requests only allow leave types flagged `is_allow_plan` (Task 5.2). */
    suspend fun listAllowPlan(): AppResult<List<LeaveTypeDto>> =
        when (val result = listActive()) {
            is AppResult.Success -> AppResult.Success(result.data.filter { it.isAllowPlan })
            is AppResult.Failure -> result
        }

    /** Admin CRUD (Task 10.1): unfiltered, including inactive rows. */
    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<LeaveTypeDto>, Int>> =
        when (val result = safeApiCall { leaveTypesApi.list(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: LeaveTypeUpsertDto): AppResult<LeaveTypeDto> = safeApiCall { leaveTypesApi.create(body) }

    suspend fun update(id: String, body: LeaveTypeUpsertDto): AppResult<LeaveTypeDto> =
        safeApiCall { leaveTypesApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { leaveTypesApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
