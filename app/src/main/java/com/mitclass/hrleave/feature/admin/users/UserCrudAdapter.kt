package com.mitclass.hrleave.feature.admin.users

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.admin.PickerOption
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.UserCreateUpsertDto
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.remote.dto.UserUpdateUpsertDto
import com.mitclass.hrleave.data.repository.TeamsRepository
import com.mitclass.hrleave.data.repository.UsersRepository

/**
 * Password required on create, optional on edit (blank = unchanged); `team_id` uses the same
 * relational-picker pattern `team_owner_id` introduced on Teams (Task 10.3).
 */
class UserCrudAdapter(
    private val usersRepository: UsersRepository,
    private val teamsRepository: TeamsRepository,
) : CrudResourceAdapter<UserDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec("email", "Email", FieldType.TEXT),
        FieldSpec("full_name", "Full name", FieldType.TEXT, required = false),
        FieldSpec("password", "Password", FieldType.PASSWORD, required = false),
        FieldSpec(
            "team_id",
            "Team",
            FieldType.PICKER,
            required = false,
            loadPickerOptions = {
                val teams = when (val result = teamsRepository.listForPicker()) {
                    is AppResult.Success -> result.data
                    is AppResult.Failure -> emptyList()
                }
                listOf(PickerOption("", "No team")) + teams
            },
        ),
        FieldSpec("is_superuser", "Superuser", FieldType.TOGGLE),
        FieldSpec("is_active", "Active", FieldType.TOGGLE),
    )

    override fun id(item: UserDto) = item.id
    override fun title(item: UserDto) = item.fullName ?: item.email
    override fun subtitle(item: UserDto) = item.email

    override fun toFormValues(item: UserDto) = mapOf(
        "email" to item.email,
        "full_name" to item.fullName.orEmpty(),
        "password" to "",
        "team_id" to item.team?.id.orEmpty(),
        "is_superuser" to item.isSuperuser.toString(),
        "is_active" to item.isActive.toString(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<UserDto>, Int>> =
        usersRepository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<UserDto> =
        usersRepository.create(
            UserCreateUpsertDto(
                email = values["email"].orEmpty(),
                password = values["password"].orEmpty(),
                fullName = values["full_name"]?.ifBlank { null },
                isSuperuser = values["is_superuser"]?.toBoolean() ?: false,
                isActive = values["is_active"]?.toBoolean() ?: true,
                teamId = values["team_id"]?.ifBlank { null },
            ),
        )

    override suspend fun update(id: String, values: Map<String, String>): AppResult<UserDto> =
        usersRepository.update(
            id,
            UserUpdateUpsertDto(
                email = values["email"]?.ifBlank { null },
                password = values["password"]?.ifBlank { null },
                fullName = values["full_name"]?.ifBlank { null },
                isSuperuser = values["is_superuser"]?.toBoolean() ?: false,
                isActive = values["is_active"]?.toBoolean() ?: true,
                teamId = values["team_id"]?.ifBlank { null },
            ),
        )

    override suspend fun delete(id: String): AppResult<Unit> = usersRepository.delete(id)
}
