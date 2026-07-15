package com.mitclass.hrleave.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    @SerialName("event_type") val eventType: String,
    @SerialName("entity_type") val entityType: String,
    @SerialName("entity_id") val entityId: String,
    val message: String,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") val createdAt: String,
    val actor: UserPresentableDto? = null,
)

@Serializable
data class NotificationsResponseDto(
    val data: List<NotificationDto>,
    val count: Int,
    @SerialName("unread_count") val unreadCount: Int,
)

@Serializable
data class UnreadCountDto(val count: Int)
