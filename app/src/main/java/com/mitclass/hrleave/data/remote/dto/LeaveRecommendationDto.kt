package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveRecommendationDto(
    @SerialName("leave_date") val leaveDate: String,
    @SerialName("bridge_holiday") val bridgeHoliday: Boolean,
    @SerialName("team_workload") val teamWorkload: Int,
    @SerialName("preference_score") val preferenceScore: Int,
    @SerialName("predicted_score") val predictedScore: Double,
)

/**
 * Distinct shape from every other list endpoint: `{leave_type_id, year, data: [...]}`, not the
 * usual `{data, count}` wrapper — not paginated, single object per year.
 */
@Serializable
data class LeaveRecommendationsResponseDto(
    @SerialName("leave_type_id") val leaveTypeId: String,
    val year: Int,
    val data: List<LeaveRecommendationDto>,
)
