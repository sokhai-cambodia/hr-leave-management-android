package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveRequestsApi
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestUpsertDto
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

    suspend fun create(body: LeaveRequestUpsertDto): AppResult<LeaveRequestDto> =
        safeApiCall { leaveRequestsApi.create(body) }

    suspend fun update(id: String, body: LeaveRequestUpsertDto): AppResult<LeaveRequestDto> =
        safeApiCall { leaveRequestsApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { leaveRequestsApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }

    /** Backend returns 422 "No approver found." when the owner has no line approver — surfaced as a friendly message. */
    suspend fun submit(id: String): AppResult<LeaveRequestDto> =
        when (val result = safeApiCall { leaveRequestsApi.submit(id) }) {
            is AppResult.Success -> result
            is AppResult.Failure -> if (result.httpCode == 422) {
                AppResult.Failure("You don't have a line approver assigned yet, contact an admin.", result.httpCode)
            } else {
                result
            }
        }

    /** Server-side filtered — not client-side — to rows where the caller is the assigned approver. */
    suspend fun listPendingForApprover(approverId: String): AppResult<List<LeaveRequestDto>> =
        when (
            val result = safeApiCall { leaveRequestsApi.list(approverId = approverId, status = "pending", limit = 100) }
        ) {
            is AppResult.Success -> AppResult.Success(result.data.data)
            is AppResult.Failure -> result
        }

    /** Reject credits the balance back — verified server-side (Task 7.1), nothing to recompute client-side. */
    suspend fun approve(id: String): AppResult<LeaveRequestDto> = safeApiCall { leaveRequestsApi.approve(id) }

    suspend fun reject(id: String): AppResult<LeaveRequestDto> = safeApiCall { leaveRequestsApi.reject(id) }
}
