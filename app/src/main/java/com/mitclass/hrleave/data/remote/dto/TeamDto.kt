package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("team_owner_id") val teamOwnerId: String,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("team_owner") val teamOwner: UserPresentableDto? = null,
)

@Serializable
data class TeamsResponseDto(val data: List<TeamDto>, val count: Int)

@Serializable
data class TeamUpsertDto(
    val name: String,
    val description: String? = null,
    @SerialName("team_owner_id") val teamOwnerId: String,
    @SerialName("is_active") val isActive: Boolean,
)
