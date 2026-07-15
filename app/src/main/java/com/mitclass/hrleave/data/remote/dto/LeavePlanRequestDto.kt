package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeavePlanDetailDto(
    val id: String,
    @SerialName("leave_date") val leaveDate: String,
)

/** `amount` is always `details.size` — trust the server value, don't recompute client-side. */
@Serializable
data class LeavePlanRequestDto(
    val id: String,
    val description: String? = null,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("leave_type_id") val leaveTypeId: String,
    val amount: Double,
    val status: String,
    @SerialName("requested_at") val requestedAt: String,
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("approver_id") val approverId: String? = null,
    @SerialName("approval_at") val approvalAt: String? = null,
    val details: List<LeavePlanDetailDto> = emptyList(),
    val owner: UserPresentableDto,
    @SerialName("leave_type") val leaveType: LeaveTypePresentableDto,
    val approver: UserPresentableDto? = null,
)

@Serializable
data class LeavePlanRequestsResponseDto(val data: List<LeavePlanRequestDto>, val count: Int)

@Serializable
data class LeavePlanDetailUpsertDto(@SerialName("leave_date") val leaveDate: String)

@Serializable
data class LeavePlanRequestUpsertDto(
    val description: String? = null,
    @SerialName("leave_type_id") val leaveTypeId: String,
    val details: List<LeavePlanDetailUpsertDto>,
)
