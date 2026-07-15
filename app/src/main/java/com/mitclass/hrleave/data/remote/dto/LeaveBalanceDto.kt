package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveBalanceDto(
    val id: String,
    val year: String,
    val balance: Double,
    @SerialName("taken_balance") val takenBalance: Double,
    @SerialName("available_balance") val availableBalance: Double,
    @SerialName("leave_type_id") val leaveTypeId: String,
    @SerialName("leave_type") val leaveType: LeaveTypePresentableDto,
    @SerialName("owner_id") val ownerId: String,
)

@Serializable
data class LeaveBalancesResponseDto(val data: List<LeaveBalanceDto>, val count: Int)
