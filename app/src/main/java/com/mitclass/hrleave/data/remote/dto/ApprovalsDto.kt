package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingApprovalsCountDto(
    @SerialName("leave_requests") val leaveRequests: Int,
    @SerialName("leave_plan_requests") val leavePlanRequests: Int,
    val total: Int,
)
