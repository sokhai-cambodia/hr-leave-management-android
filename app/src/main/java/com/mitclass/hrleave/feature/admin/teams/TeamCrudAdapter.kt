package com.mitclass.hrleave.feature.admin.teams

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.TeamDto
import com.mitclass.hrleave.data.remote.dto.TeamUpsertDto
import com.mitclass.hrleave.data.repository.TeamsRepository
import com.mitclass.hrleave.data.repository.UsersRepository

/** `team_owner_id` needs a searchable user-picker, not raw UUID entry — the pattern's first relational field (Task 10.3). */
class TeamCrudAdapter(
    private val teamsRepository: TeamsRepository,
    usersRepository: UsersRepository,
) : CrudResourceAdapter<TeamDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec("name", "Name", FieldType.TEXT),
        FieldSpec("description", "Description", FieldType.MULTILINE_TEXT, required = false),
        FieldSpec(
            "team_owner_id",
            "Team owner",
            FieldType.PICKER,
            loadPickerOptions = { usersRepository.listForPicker().let { if (it is AppResult.Success) it.data else emptyList() } },
        ),
        FieldSpec("is_active", "Active", FieldType.TOGGLE),
    )

    override fun id(item: TeamDto) = item.id
    override fun title(item: TeamDto) = item.name
    override fun subtitle(item: TeamDto) = item.teamOwner?.fullName ?: item.teamOwner?.email.orEmpty()

    override fun toFormValues(item: TeamDto) = mapOf(
        "name" to item.name,
        "description" to item.description.orEmpty(),
        "team_owner_id" to item.teamOwnerId,
        "is_active" to item.isActive.toString(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<TeamDto>, Int>> =
        teamsRepository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<TeamDto> =
        teamsRepository.create(toUpsertDto(values))

    override suspend fun update(id: String, values: Map<String, String>): AppResult<TeamDto> =
        teamsRepository.update(id, toUpsertDto(values))

    override suspend fun delete(id: String): AppResult<Unit> = teamsRepository.delete(id)

    private fun toUpsertDto(values: Map<String, String>) = TeamUpsertDto(
        name = values["name"].orEmpty(),
        description = values["description"]?.ifBlank { null },
        teamOwnerId = values["team_owner_id"].orEmpty(),
        isActive = values["is_active"]?.toBoolean() ?: true,
    )
}
