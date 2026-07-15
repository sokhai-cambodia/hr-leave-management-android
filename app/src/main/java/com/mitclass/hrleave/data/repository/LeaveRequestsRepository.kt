package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveRequestsApi
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveRequestsRepository @Inject constructor(
    private val leaveRequestsApi: LeaveRequestsApi,
) {
    /** Own submissions only, regardless of role — a superuser browsing this screen still only sees theirs. */
    suspend fun listMine(ownerId: String, skip: Int, limit: Int): AppResult<Pair<List<LeaveRequestDto>, Int>> =
        when (val result = safeApiCall { leaveRequestsApi.list(ownerId = ownerId, skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun get(id: String): AppResult<LeaveRequestDto> = safeApiCall { leaveRequestsApi.get(id) }
}
