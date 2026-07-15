package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_superuser") val isSuperuser: Boolean = false,
    @SerialName("full_name") val fullName: String? = null,
    val team: TeamPresentableDto? = null,
)

@Serializable
data class UserPresentableDto(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    val email: String,
)

@Serializable
data class TeamPresentableDto(
    val id: String,
    val name: String,
    @SerialName("team_owner") val teamOwner: UserPresentableDto? = null,
)

@Serializable
data class LeaveTypePresentableDto(
    val id: String,
    val code: String,
    val name: String,
)

@Serializable
data class TokenDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
)

@Serializable
data class UsersResponseDto(val data: List<UserDto>, val count: Int)

/** Password is required here — create-only, per the backend's UserCreate model. */
@Serializable
data class UserCreateUpsertDto(
    val email: String,
    val password: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("is_superuser") val isSuperuser: Boolean,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("team_id") val teamId: String? = null,
)

/** Password is optional here (blank = leave unchanged) — `explicitNulls = false` omits it from the wire entirely when null. */
@Serializable
data class UserUpdateUpsertDto(
    val email: String? = null,
    val password: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("is_superuser") val isSuperuser: Boolean,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("team_id") val teamId: String? = null,
)
