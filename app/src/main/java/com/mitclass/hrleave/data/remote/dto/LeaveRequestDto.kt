package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Status literals are exactly "draft" | "pending" | "approved" | "rejected", lowercase. */
@Serializable
data class LeaveRequestDto(
    val id: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val description: String? = null,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("leave_type_id") val leaveTypeId: String,
    val amount: Double,
    val status: String,
    @SerialName("requested_at") val requestedAt: String,
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("approver_id") val approverId: String? = null,
    @SerialName("approval_at") val approvalAt: String? = null,
    val owner: UserPresentableDto,
    @SerialName("leave_type") val leaveType: LeaveTypePresentableDto,
    val approver: UserPresentableDto? = null,
)

@Serializable
data class LeaveRequestsResponseDto(val data: List<LeaveRequestDto>, val count: Int)

@Serializable
data class LeaveRequestUpsertDto(
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val description: String? = null,
    @SerialName("leave_type_id") val leaveTypeId: String,
)
