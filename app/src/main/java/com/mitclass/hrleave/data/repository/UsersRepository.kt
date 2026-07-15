package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.admin.PickerOption
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.UsersApi
import com.mitclass.hrleave.data.remote.dto.UserCreateUpsertDto
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.remote.dto.UserUpdateUpsertDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
) {
    suspend fun listAll(skip: Int, limit: Int): AppResult<Pair<List<UserDto>, Int>> =
        when (val result = safeApiCall { usersApi.list(skip = skip, limit = limit) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun create(body: UserCreateUpsertDto): AppResult<UserDto> = safeApiCall { usersApi.create(body) }

    suspend fun update(id: String, body: UserUpdateUpsertDto): AppResult<UserDto> =
        safeApiCall { usersApi.update(id, body) }

    suspend fun delete(id: String): AppResult<Unit> =
        when (val result = safeApiCall { usersApi.delete(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }

    /** For the Teams admin form's team_owner_id relational picker (Task 10.3). */
    suspend fun listForPicker(): AppResult<List<PickerOption>> =
        when (val result = listAll(0, 100)) {
            is AppResult.Success -> AppResult.Success(
                result.data.first.map { PickerOption(it.id, it.fullName ?: it.email) },
            )
            is AppResult.Failure -> result
        }
}
