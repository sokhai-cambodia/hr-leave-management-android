package com.mitclass.hrleave.feature.leaveplanrequests

import java.time.LocalDate

/**
 * Client-side duplicate-date rejection for the multi-date picker — kept as a pure, directly
 * unit-tested function rather than buried in a Composable callback (Task 5.2).
 *
 * Returns `null` if [newDate] is already in [existingDates] (caller shows a message, fires no
 * network call); otherwise the appended list, sorted chronologically.
 */
fun addDateIfNotDuplicate(existingDates: List<LocalDate>, newDate: LocalDate): List<LocalDate>? {
    if (existingDates.contains(newDate)) return null
    return (existingDates + newDate).sorted()
}
