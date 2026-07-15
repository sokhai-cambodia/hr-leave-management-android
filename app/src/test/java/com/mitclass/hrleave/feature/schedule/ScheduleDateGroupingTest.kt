package com.mitclass.hrleave.feature.schedule

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

private data class Entry(val label: String, val start: LocalDate, val end: LocalDate)

class ScheduleDateGroupingTest {

    private val start = { e: Entry -> e.start }
    private val end = { e: Entry -> e.end }

    @Test
    fun `single-day entry lands in exactly one bucket`() {
        val entry = Entry("holiday", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 1))
        val grouped = groupByDay(listOf(entry), start, end)

        assertEquals(setOf(LocalDate.of(2026, 1, 1)), grouped.keys)
        assertEquals(listOf(entry), grouped[LocalDate.of(2026, 1, 1)])
    }

    @Test
    fun `multi-day range spanning a year boundary is expanded across every day`() {
        val entry = Entry("leave", LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 2))
        val grouped = groupByDay(listOf(entry), start, end)

        val expectedDays = setOf(
            LocalDate.of(2025, 12, 30),
            LocalDate.of(2025, 12, 31),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 2),
        )
        assertEquals(expectedDays, grouped.keys)
        expectedDays.forEach { day -> assertEquals(listOf(entry), grouped[day]) }
    }

    @Test
    fun `filterByMonth keeps an entry that only overlaps the boundary month`() {
        val decemberSpillover = Entry("leave", LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 2))

        val decemberResult = filterByMonth(listOf(decemberSpillover), 2025, 12, start, end)
        val januaryResult = filterByMonth(listOf(decemberSpillover), 2026, 1, start, end)
        val februaryResult = filterByMonth(listOf(decemberSpillover), 2026, 2, start, end)

        assertTrue(decemberResult.contains(decemberSpillover))
        assertTrue(januaryResult.contains(decemberSpillover))
        assertTrue(februaryResult.isEmpty())
    }

    @Test
    fun `filterByMonth excludes entries entirely outside the month`() {
        val marchEntry = Entry("holiday", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 15))

        val result = filterByMonth(listOf(marchEntry), 2026, 4, start, end)

        assertTrue(result.isEmpty())
    }
}
