package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.admin.PickerOption
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.TeamsApi
import com.mitclass.hrleave.data.remote.dto.TeamDto
import com.mitclass.hrleave.data.remote.dto.TeamUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

/** Powers the team-owner detection heuristic (Task 2.2) and the Teams admin screen (Task 10.3). */
@Singleton
class TeamsRepository @Inject constructor(
    private val teamsApi: TeamsApi,
) {
    suspend fun listTeams(): AppResult<List<TeamDto>> =
        when (val result = safeApiCall { teamsApi.listTeams() }) {
            is AppResult.Success -> AppResult.Success(result.data.data)
            is AppResult.Failure -> result
        }

    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<TeamDto>, Int>> =
        when (val result = safeApiCall { teamsApi.listTeams(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: TeamUpsertDto): AppResult<TeamDto> = safeApiCall { teamsApi.create(body) }

    suspend fun update(id: String, body: TeamUpsertDto): AppResult<TeamDto> = safeApiCall { teamsApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { teamsApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }

    /** For the Users admin form's team_id relational picker (Task 10.3). */
    suspend fun listForPicker(): AppResult<List<PickerOption>> =
        when (val result = listTeams()) {
            is AppResult.Success -> AppResult.Success(result.data.map { PickerOption(it.id, it.name) })
            is AppResult.Failure -> result
        }
}
