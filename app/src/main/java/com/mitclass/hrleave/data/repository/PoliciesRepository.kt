package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.PoliciesApi
import com.mitclass.hrleave.data.remote.dto.PolicyDto
import com.mitclass.hrleave.data.remote.dto.PolicyUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoliciesRepository @Inject constructor(
    private val policiesApi: PoliciesApi,
) {
    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<PolicyDto>, Int>> =
        when (val result = safeApiCall { policiesApi.list(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: PolicyUpsertDto): AppResult<PolicyDto> = safeApiCall { policiesApi.create(body) }

    suspend fun update(id: String, body: PolicyUpsertDto): AppResult<PolicyDto> =
        safeApiCall { policiesApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { policiesApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
