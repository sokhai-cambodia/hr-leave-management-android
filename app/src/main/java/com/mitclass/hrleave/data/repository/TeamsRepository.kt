package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.TeamsApi
import com.mitclass.hrleave.data.remote.dto.TeamDto
import javax.inject.Inject
import javax.inject.Singleton

/** Powers both the team-owner detection heuristic (Task 2.2) and the Teams admin screen (Phase 10). */
@Singleton
class TeamsRepository @Inject constructor(
    private val teamsApi: TeamsApi,
) {
    suspend fun listTeams(): AppResult<List<TeamDto>> =
        when (val result = safeApiCall { teamsApi.listTeams() }) {
            is AppResult.Success -> AppResult.Success(result.data.data)
            is AppResult.Failure -> result
        }
}
