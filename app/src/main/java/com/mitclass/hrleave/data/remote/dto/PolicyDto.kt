package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `operation`/`value` are free-form strings ("in", ">", "<", ">=", "<=", "=="/"[0,4]", "50%") — no client-side enum validation. */
@Serializable
data class PolicyDto(
    val id: String,
    val code: String,
    val name: String,
    val operation: String? = "==",
    val value: String,
    val score: Double? = 0.0,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class PoliciesResponseDto(val data: List<PolicyDto>, val count: Int)

@Serializable
data class PolicyUpsertDto(
    val code: String,
    val name: String,
    val operation: String,
    val value: String,
    val score: Double,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean,
)
