package com.mitclass.hrleave.feature.reports

import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanDetailDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypePresentableDto
import com.mitclass.hrleave.data.remote.dto.UserPresentableDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

private val owner = UserPresentableDto(id = "u1", fullName = "Ada Lovelace", email = "ada@example.com")
private val annualLeaveType = LeaveTypePresentableDto(id = "lt1", code = "AL", name = "Annual Leave")
private val sickLeaveType = LeaveTypePresentableDto(id = "lt2", code = "SL", name = "Sick Leave")

private fun leaveRequest(
    status: String,
    startDate: String = "2026-01-10",
    endDate: String = "2026-01-12",
    amount: Double = 3.0,
    leaveType: LeaveTypePresentableDto = annualLeaveType,
    requestedAt: String = "2026-01-01T09:00:00Z",
    submittedAt: String? = null,
    approvalAt: String? = null,
) = LeaveRequestDto(
    id = "r-$status-$startDate",
    startDate = startDate,
    endDate = endDate,
    ownerId = owner.id,
    leaveTypeId = leaveType.id,
    amount = amount,
    status = status,
    requestedAt = requestedAt,
    submittedAt = submittedAt,
    approvalAt = approvalAt,
    owner = owner,
    leaveType = leaveType,
)

private fun leavePlanRequest(
    status: String,
    amount: Double = 2.0,
    leaveType: LeaveTypePresentableDto = annualLeaveType,
) = LeavePlanRequestDto(
    id = "p-$status",
    ownerId = owner.id,
    leaveTypeId = leaveType.id,
    amount = amount,
    status = status,
    requestedAt = "2026-01-01T09:00:00Z",
    details = listOf(LeavePlanDetailDto(id = "d1", leaveDate = "2026-01-15")),
    owner = owner,
    leaveType = leaveType,
)

class ReportsCalculationsTest {

    @Test
    fun `parseServerDateTime accepts both offset and plain local datetimes`() {
        assertEquals(LocalDate.of(2026, 1, 1), parseServerDateTime("2026-01-01T09:00:00Z")?.toLocalDate())
        assertEquals(LocalDate.of(2026, 1, 1), parseServerDateTime("2026-01-01T09:00:00")?.toLocalDate())
        assertNull(parseServerDateTime("not-a-date"))
    }

    @Test
    fun `buildPersonalReport combines request and plan-request status counts`() {
        val balances = listOf(
            LeaveBalanceDto(
                id = "b1",
                year = "2026",
                balance = 12.0,
                takenBalance = 4.0,
                availableBalance = 8.0,
                leaveTypeId = annualLeaveType.id,
                leaveType = annualLeaveType,
                ownerId = owner.id,
            ),
        )
        val requests = listOf(leaveRequest("approved"), leaveRequest("pending"))
        val planRequests = listOf(leavePlanRequest("approved"))

        val report = buildPersonalReport(balances, requests, planRequests)

        assertEquals(1, report.balances.size)
        assertEquals(4.0, report.balances.first().takenDays, 0.0)
        assertEquals(mapOf("approved" to 2, "pending" to 1), report.statusCounts)
    }

    @Test
    fun `buildTeamReport sums approved days per leave type across both request kinds`() {
        val requests = listOf(
            leaveRequest("approved", amount = 3.0, leaveType = annualLeaveType),
            leaveRequest("rejected", amount = 5.0, leaveType = sickLeaveType),
        )
        val planRequests = listOf(leavePlanRequest("approved", amount = 2.0, leaveType = annualLeaveType))

        val report = buildTeamReport(requests, planRequests, today = LocalDate.of(2026, 1, 1))

        assertEquals(5.0, report.approvedDaysByLeaveType[annualLeaveType.name]!!, 0.0)
        assertNull(report.approvedDaysByLeaveType[sickLeaveType.name])
        assertEquals(3, report.totalCount)
    }

    @Test
    fun `buildTeamReport marks someone currently out only while today falls in their approved range`() {
        val requests = listOf(leaveRequest("approved", startDate = "2026-01-10", endDate = "2026-01-12"))

        val before = buildTeamReport(requests, emptyList(), today = LocalDate.of(2026, 1, 9))
        val during = buildTeamReport(requests, emptyList(), today = LocalDate.of(2026, 1, 11))
        val after = buildTeamReport(requests, emptyList(), today = LocalDate.of(2026, 1, 13))

        assertTrue(before.currentlyOut.isEmpty())
        assertEquals(1, during.currentlyOut.size)
        assertEquals("Ada Lovelace", during.currentlyOut.first().name)
        assertTrue(after.currentlyOut.isEmpty())
    }

    @Test
    fun `buildTeamReport computes average approval turnaround only from decided requests`() {
        val requests = listOf(
            leaveRequest(
                "approved",
                submittedAt = "2026-01-01T09:00:00Z",
                approvalAt = "2026-01-01T15:00:00Z",
            ),
            leaveRequest("draft", submittedAt = null, approvalAt = null),
        )

        val report = buildTeamReport(requests, emptyList(), today = LocalDate.of(2026, 1, 1))

        assertEquals(6.0, report.avgApprovalTurnaroundHours!!, 0.001)
    }

    @Test
    fun `buildTeamReport turnaround is null when nothing has been decided yet`() {
        val requests = listOf(leaveRequest("pending", submittedAt = "2026-01-01T09:00:00Z", approvalAt = null))

        val report = buildTeamReport(requests, emptyList(), today = LocalDate.of(2026, 1, 1))

        assertNull(report.avgApprovalTurnaroundHours)
    }
}
