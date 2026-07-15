package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.NotificationsApi
import com.mitclass.hrleave.data.remote.dto.NotificationDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val notificationsApi: NotificationsApi,
) {
    suspend fun fetchNotifications(skip: Int, limit: Int, isRead: Boolean? = null): AppResult<Pair<List<NotificationDto>, Int>> =
        when (val result = safeApiCall { notificationsApi.list(skip = skip, limit = limit, isRead = isRead) }) {
            is AppResult.Success -> AppResult.Success(result.data.data to result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun fetchUnreadCount(): AppResult<Int> =
        when (val result = safeApiCall { notificationsApi.unreadCount() }) {
            is AppResult.Success -> AppResult.Success(result.data.count)
            is AppResult.Failure -> result
        }

    suspend fun markRead(id: String): AppResult<Unit> =
        when (val result = safeApiCall { notificationsApi.markRead(id) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }

    suspend fun markAllRead(): AppResult<Unit> =
        when (val result = safeApiCall { notificationsApi.markAllRead() }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
}
