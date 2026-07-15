package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewPasswordDto(
    val token: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class MessageDto(val message: String)
