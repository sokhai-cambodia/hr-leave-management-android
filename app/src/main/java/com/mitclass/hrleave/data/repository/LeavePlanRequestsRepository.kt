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

    /** No balance debit here (unlike Leave Requests) — plan requests don't touch balances on submit. */
    suspend fun submit(id: String): AppResult<LeavePlanRequestDto> =
        when (val result = safeApiCall { leavePlanRequestsApi.submit(id) }) {
            is AppResult.Success -> result
            is AppResult.Failure -> if (result.httpCode == 422) {
                AppResult.Failure("You don't have a line approver assigned yet, contact an admin.", result.httpCode)
            } else {
                result
            }
        }

    suspend fun listPendingForApprover(approverId: String): AppResult<List<LeavePlanRequestDto>> =
        when (
            val result =
                safeApiCall { leavePlanRequestsApi.list(approverId = approverId, status = "pending", limit = 100) }
        ) {
            is AppResult.Success -> AppResult.Success(result.data.data)
            is AppResult.Failure -> result
        }

    /** No balance credit on reject here (unlike Leave Requests) — plan requests never touch balances. */
    suspend fun approve(id: String): AppResult<LeavePlanRequestDto> = safeApiCall { leavePlanRequestsApi.approve(id) }

    suspend fun reject(id: String): AppResult<LeavePlanRequestDto> = safeApiCall { leavePlanRequestsApi.reject(id) }
}
