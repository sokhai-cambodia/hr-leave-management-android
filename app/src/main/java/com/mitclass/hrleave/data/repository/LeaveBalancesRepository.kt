package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveBalancesApi
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveBalancesRepository @Inject constructor(
    private val leaveBalancesApi: LeaveBalancesApi,
) {
    suspend fun getMyBalances(): AppResult<List<LeaveBalanceDto>> =
        when (val result = safeApiCall { leaveBalancesApi.getMyBalances() }) {
            is AppResult.Success -> AppResult.Success(result.data.data)
            is AppResult.Failure -> result
        }

    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<LeaveBalanceDto>, Int>> =
        when (val result = safeApiCall { leaveBalancesApi.list(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: LeaveBalanceUpsertDto): AppResult<LeaveBalanceDto> =
        safeApiCall { leaveBalancesApi.create(body) }

    suspend fun update(id: String, body: LeaveBalanceUpsertDto): AppResult<LeaveBalanceDto> =
        safeApiCall { leaveBalancesApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { leaveBalancesApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
