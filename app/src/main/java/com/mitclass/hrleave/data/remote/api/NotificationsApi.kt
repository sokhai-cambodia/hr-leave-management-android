package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.NotificationsResponseDto
import com.mitclass.hrleave.data.remote.dto.UnreadCountDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {
    @GET("notifications/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
        @Query("is_read") isRead: Boolean? = null,
    ): NotificationsResponseDto

    @GET("notifications/unread-count")
    suspend fun unreadCount(): UnreadCountDto

    @PUT("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: String): MessageDto

    @PUT("notifications/mark-all-read")
    suspend fun markAllRead(): MessageDto
}
