package com.mitclass.hrleave.feature.reports

import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.LeavePlanRequestDto
import com.mitclass.hrleave.data.remote.dto.LeaveRequestDto
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Pure aggregation logic behind [ReportsViewModel] — kept dependency-free so it's directly unit-testable. */

fun parseLocalDate(raw: String): LocalDate? = runCatching { LocalDate.parse(raw) }.getOrNull()

fun parseServerDateTime(raw: String): LocalDateTime? =
    runCatching { OffsetDateTime.parse(raw).toLocalDateTime() }
        .recoverCatching { LocalDateTime.parse(raw) }
        .getOrNull()

fun buildPersonalReport(
    balances: List<LeaveBalanceDto>,
    requests: List<LeaveRequestDto>,
    planRequests: List<LeavePlanRequestDto>,
): PersonalReport {
    val balanceEntries = balances.map {
        LeaveTypeBalance(
            leaveTypeName = it.leaveType.name,
            takenDays = it.takenBalance,
            availableDays = it.availableBalance,
        )
    }
    val statusCounts = (requests.map { it.status } + planRequests.map { it.status })
        .groupingBy { it }
        .eachCount()
    return PersonalReport(balances = balanceEntries, statusCounts = statusCounts)
}

fun buildTeamReport(
    requests: List<LeaveRequestDto>,
    planRequests: List<LeavePlanRequestDto>,
    today: LocalDate = LocalDate.now(),
): TeamReport {
    val statusCounts = (requests.map { it.status } + planRequests.map { it.status })
        .groupingBy { it }
        .eachCount()

    val approvedDaysByLeaveType = mutableMapOf<String, Double>()
    requests.filter { it.status == "approved" }.forEach {
        approvedDaysByLeaveType.merge(it.leaveType.name, it.amount, Double::plus)
    }
    planRequests.filter { it.status == "approved" }.forEach {
        approvedDaysByLeaveType.merge(it.leaveType.name, it.amount, Double::plus)
    }

    val currentlyOut = requests
        .filter { it.status == "approved" }
        .mapNotNull { request ->
            val start = parseLocalDate(request.startDate) ?: return@mapNotNull null
            val end = parseLocalDate(request.endDate) ?: return@mapNotNull null
            if (!today.isBefore(start) && !today.isAfter(end)) {
                CurrentlyOutEntry(
                    name = request.owner.fullName ?: request.owner.email,
                    leaveTypeName = request.leaveType.name,
                    returnsOn = request.endDate,
                )
            } else {
                null
            }
        }

    val turnaroundHours = (requests.map { it.submittedAt to it.approvalAt } + planRequests.map { it.submittedAt to it.approvalAt })
        .mapNotNull { (submitted, approved) ->
            val submittedTime = submitted?.let { parseServerDateTime(it) } ?: return@mapNotNull null
            val approvedTime = approved?.let { parseServerDateTime(it) } ?: return@mapNotNull null
            Duration.between(submittedTime, approvedTime).toMinutes() / 60.0
        }
    val avgTurnaround = turnaroundHours.takeIf { it.isNotEmpty() }?.average()

    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val monthlyCounts = (requests.map { it.requestedAt } + planRequests.map { it.requestedAt })
        .mapNotNull { parseServerDateTime(it) }
        .groupingBy { it.format(monthFormatter) }
        .eachCount()
    val monthlyTrend = monthlyCounts.entries
        .sortedBy { it.key }
        .takeLast(6)
        .map { (key, count) ->
            val month = YearMonth.parse(key, monthFormatter)
            month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) to count
        }

    return TeamReport(
        totalCount = requests.size + planRequests.size,
        statusCounts = statusCounts,
        approvedDaysByLeaveType = approvedDaysByLeaveType,
        currentlyOut = currentlyOut,
        avgApprovalTurnaroundHours = avgTurnaround,
        monthlyTrend = monthlyTrend,
    )
}
