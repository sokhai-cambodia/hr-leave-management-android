package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveTypesApi
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
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
}
