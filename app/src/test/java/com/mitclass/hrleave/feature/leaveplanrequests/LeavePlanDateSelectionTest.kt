package com.mitclass.hrleave.feature.leaveplanrequests

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class LeavePlanDateSelectionTest {

    @Test
    fun `appends a new date and keeps the list sorted`() {
        val existing = listOf(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 5))
        val result = addDateIfNotDuplicate(existing, LocalDate.of(2026, 3, 8))

        assertEquals(
            listOf(LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 8), LocalDate.of(2026, 3, 10)),
            result,
        )
    }

    @Test
    fun `rejects a duplicate date without mutating the list`() {
        val existing = listOf(LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 10))
        val result = addDateIfNotDuplicate(existing, LocalDate.of(2026, 3, 5))

        assertNull(result)
    }

    @Test
    fun `first date on an empty list is accepted`() {
        val result = addDateIfNotDuplicate(emptyList(), LocalDate.of(2026, 1, 1))

        assertEquals(listOf(LocalDate.of(2026, 1, 1)), result)
    }
}
