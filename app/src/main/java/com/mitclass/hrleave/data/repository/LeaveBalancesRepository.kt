package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.LeaveBalancesApi
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
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
}
