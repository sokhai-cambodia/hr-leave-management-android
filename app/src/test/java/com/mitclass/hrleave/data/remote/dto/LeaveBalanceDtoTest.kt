package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class LeaveBalanceDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parses balance, taken_balance and available_balance from the wire shape`() {
        val body = """
            {
                "id": "b1",
                "year": "2026",
                "balance": 18.0,
                "taken_balance": 5.5,
                "available_balance": 12.5,
                "leave_type_id": "lt1",
                "leave_type": {"id": "lt1", "code": "AL", "name": "Annual Leave"},
                "owner_id": "u1"
            }
        """.trimIndent()

        val dto = json.decodeFromString(LeaveBalanceDto.serializer(), body)

        assertEquals(18.0, dto.balance, 0.0)
        assertEquals(5.5, dto.takenBalance, 0.0)
        assertEquals(12.5, dto.availableBalance, 0.0)
        assertEquals("Annual Leave", dto.leaveType.name)
    }

    @Test
    fun `available_balance is trusted from the server, not recomputed client-side`() {
        // Deliberately inconsistent with balance - taken_balance, to prove the DTO
        // just carries the server's number through rather than deriving its own.
        val body = """
            {
                "id": "b1",
                "year": "2026",
                "balance": 10.0,
                "taken_balance": 1.0,
                "available_balance": 0.0,
                "leave_type_id": "lt1",
                "leave_type": {"id": "lt1", "code": "AL", "name": "Annual Leave"},
                "owner_id": "u1"
            }
        """.trimIndent()

        val dto = json.decodeFromString(LeaveBalanceDto.serializer(), body)

        assertEquals(0.0, dto.availableBalance, 0.0)
    }
}
