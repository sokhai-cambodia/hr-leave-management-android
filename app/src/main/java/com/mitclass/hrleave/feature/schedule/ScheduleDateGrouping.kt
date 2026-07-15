package com.mitclass.hrleave.feature.schedule

import java.time.LocalDate

/**
 * Expands each entry across its inclusive [startDate, endDate] range and buckets it by day —
 * feeds the calendar's per-day markers. A single-day entry (holiday, or a plan-request detail
 * where start == end) lands in exactly one bucket.
 */
fun <T> groupByDay(entries: List<T>, startDate: (T) -> LocalDate, endDate: (T) -> LocalDate): Map<LocalDate, List<T>> {
    val result = mutableMapOf<LocalDate, MutableList<T>>()
    for (entry in entries) {
        var day = startDate(entry)
        val end = endDate(entry)
        while (!day.isAfter(end)) {
            result.getOrPut(day) { mutableListOf() }.add(entry)
            day = day.plusDays(1)
        }
    }
    return result
}

/** Entries whose range overlaps the given [year]/[month] — feeds the list sections below the calendar. */
fun <T> filterByMonth(
    entries: List<T>,
    year: Int,
    month: Int,
    startDate: (T) -> LocalDate,
    endDate: (T) -> LocalDate,
): List<T> {
    val monthStart = LocalDate.of(year, month, 1)
    val monthEnd = monthStart.plusMonths(1).minusDays(1)
    return entries.filter { entry -> !endDate(entry).isBefore(monthStart) && !startDate(entry).isAfter(monthEnd) }
}
