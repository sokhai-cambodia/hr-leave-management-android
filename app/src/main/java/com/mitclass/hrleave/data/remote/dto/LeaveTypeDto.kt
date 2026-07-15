package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveTypeDto(
    val id: String,
    val code: String,
    val name: String,
    val entitlement: Int = 0,
    val description: String? = null,
    @SerialName("is_allow_plan") val isAllowPlan: Boolean = true,
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class LeaveTypesResponseDto(val data: List<LeaveTypeDto>, val count: Int)

@Serializable
data class LeaveTypeUpsertDto(
    val code: String,
    val name: String,
    val entitlement: Int,
    val description: String? = null,
    @SerialName("is_allow_plan") val isAllowPlan: Boolean,
    @SerialName("is_active") val isActive: Boolean,
)
