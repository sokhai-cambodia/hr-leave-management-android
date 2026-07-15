package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `date` is a plain "YYYY-MM-DD" string on the wire — only the date-picker UI treats it as a date. */
@Serializable
data class PublicHolidayDto(
    val id: String,
    val date: String,
    val name: String,
    val description: String? = null,
)

@Serializable
data class PublicHolidaysResponseDto(val data: List<PublicHolidayDto>, val count: Int)

@Serializable
data class PublicHolidayUpsertDto(
    val date: String,
    val name: String,
    val description: String? = null,
)

@Serializable
data class ScheduleTeamLeaveEntryDto(
    val id: String,
    val source: String,
    val owner: UserPresentableDto,
    @SerialName("leave_type") val leaveType: LeaveTypePresentableDto,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
)

@Serializable
data class ScheduleDto(
    val year: Int,
    val month: Int,
    @SerialName("public_holidays") val publicHolidays: List<PublicHolidayDto>,
    @SerialName("team_leave") val teamLeave: List<ScheduleTeamLeaveEntryDto>,
)
