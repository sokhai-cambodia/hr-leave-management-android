package com.mitclass.hrleave.core.errors

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiErrorTest {

    @Test
    fun `parses simple string detail`() {
        val body = """{"detail": "Incorrect email or password"}"""
        assertEquals("Incorrect email or password", ApiError.parse(body))
    }

    @Test
    fun `parses 422 validation list detail into one readable message`() {
        val body = """
            {"detail": [
                {"loc": ["body", "password"], "msg": "field required", "type": "value_error.missing"},
                {"loc": ["body", "email"], "msg": "invalid email format", "type": "value_error"}
            ]}
        """.trimIndent()
        val message = ApiError.parse(body)
        assertTrue(message.contains("password: field required"))
        assertTrue(message.contains("email: invalid email format"))
    }

    @Test
    fun `falls back to default message on unparseable body`() {
        assertEquals(ApiError.DEFAULT_MESSAGE, ApiError.parse("not json"))
    }

    @Test
    fun `falls back to default message on null or blank body`() {
        assertEquals(ApiError.DEFAULT_MESSAGE, ApiError.parse(null))
        assertEquals(ApiError.DEFAULT_MESSAGE, ApiError.parse(""))
    }

    @Test
    fun `falls back to default message when detail key is missing`() {
        assertEquals(ApiError.DEFAULT_MESSAGE, ApiError.parse("{}"))
    }
}
