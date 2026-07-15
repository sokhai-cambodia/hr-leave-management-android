package com.mitclass.hrleave.core.errors

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * FastAPI returns errors in one of two shapes: `{"detail": "some message"}` for most
 * errors, or `{"detail": [{"loc": [...], "msg": "...", "type": "..."}]}` for 422
 * validation failures. This flattens both into one human-readable message.
 */
object ApiError {

    const val DEFAULT_MESSAGE = "Something went wrong. Please try again."
    const val NETWORK_ERROR_MESSAGE = "Can't reach the server. Check your connection and try again."

    @Serializable
    private data class Envelope(val detail: JsonElement? = null)

    @Serializable
    private data class ValidationErrorItem(
        val loc: List<JsonElement> = emptyList(),
        val msg: String = "",
    )

    private val json = Json { ignoreUnknownKeys = true }

    fun parse(body: String?): String {
        if (body.isNullOrBlank()) return DEFAULT_MESSAGE
        return try {
            val envelope = json.decodeFromString(Envelope.serializer(), body)
            messageFromDetail(envelope.detail) ?: DEFAULT_MESSAGE
        } catch (e: Exception) {
            DEFAULT_MESSAGE
        }
    }

    private fun messageFromDetail(detail: JsonElement?): String? = when (detail) {
        null -> null
        is JsonArray -> detail
            .mapNotNull { element -> runCatching { messageFromValidationItem(element) }.getOrNull() }
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .ifBlank { null }
        else -> (detail as? JsonPrimitive)?.contentOrNull
    }

    private fun messageFromValidationItem(element: JsonElement): String {
        val item = json.decodeFromJsonElement(ValidationErrorItem.serializer(), element)
        val field = item.loc.lastOrNull()?.let { (it as? JsonPrimitive)?.contentOrNull }
        return if (!field.isNullOrBlank() && field != "body") "$field: ${item.msg}" else item.msg
    }
}
